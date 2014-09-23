/*******************************************************************************
 * Copyright (c) 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.amazonaws.services.cloudtrail.clientlibrary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.configuration.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.manager.AWSCloudTrailClientS3Manager;
import com.amazonaws.services.cloudtrail.clientlibrary.manager.AWSCloudTrailClientSqsManager;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.clientlibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.BasicProcessLogInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.BasicProcessSourceInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.clientlibrary.serializer.AWSCloudTrailRecordSerializer;
import com.amazonaws.services.cloudtrail.clientlibrary.serializer.DefaultRecordSerializer;
import com.amazonaws.services.cloudtrail.clientlibrary.serializer.RawLogDeliveryRecordSerializer;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.RecordBuffer;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AWSCloudTrailRecordReader is responsible to process a stream of records. It parses each record and hand
 * the records to RecordsProcessor to process.
 */
public class AWSCloudTrailClientRecordReader {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailClientRecordReader.class);

    private final SourceFilter sourceFilter;
    private final RecordFilter recordFilter;
    private final RecordsProcessor recordsProcessor;
    private final ProgressReporter progressReporter;
    private final ExceptionHandler exceptionHandler;

    private AWSCloudTrailClientConfiguration config;

    private AWSCloudTrailClientSqsManager sqsManager;
    private AWSCloudTrailClientS3Manager s3Manager;

    /**
     * Jackson parser to parse CloudTrail log files.
     */
    private ObjectMapper mapper;

    /**
     * Internal use only.
     *
     * This constructor creates an instance of AWSCloudTrailRecordReader object.
     *
     * @param recordsProcesor user's implementation of recordsProcesor
     * @param sourceFilter user's implementation of sourceFilter
     * @param recordFilter user's implementation of recordFilter
     * @param progressReporter user's implementation of progressReporter
     * @param exceptionHandler user's implementation of exceptionHandler
     * @param sqsManager that poll message from SQS queue
     * @param s3Manager that download CloudTrail log files from S3
     * @param configuration user provided AWSCloudTrailClientLibrary configuration
     */
    protected AWSCloudTrailClientRecordReader(RecordsProcessor recordsProcesor, SourceFilter sourceFilter, RecordFilter recordFilter,
            ProgressReporter progressReporter, ExceptionHandler exceptionHandler, AWSCloudTrailClientSqsManager sqsManager,
            AWSCloudTrailClientS3Manager s3Manager, AWSCloudTrailClientConfiguration configuration) {

        this.recordsProcessor = recordsProcesor;
        this.sourceFilter = sourceFilter;
        this.recordFilter = recordFilter;
        this.progressReporter = progressReporter;
        this.exceptionHandler = exceptionHandler;
        this.config = configuration;

        this.sqsManager = sqsManager;
        this.s3Manager = s3Manager;

        this.mapper = new ObjectMapper();
    }

    /**
     * Poll messages from SQS queue and convert messages to CloudTrailSource.
     *
     * @return
     */
    public List<CloudTrailSource> getSources() {
        List<Message> sqsMessages = this.sqsManager.pollQueue();
        List<CloudTrailSource> sources = this.sqsManager.parseMessage(sqsMessages);
        return sources;
    }

    /**
     * Retrieve S3 object URL from source then download the object process each record through
     * call back functions.
     *
     * @param source CloudTrailSource to process
     */
    public void processSource (CloudTrailSource source) {
        // Start to process the source
        boolean processSourceSuccess = false;
        ProgressStatus startProcessSource = new ProgressStatus(ProgressState.processSource, new BasicProcessSourceInfo(source, processSourceSuccess));
        final Object processSourceReportObject = this.progressReporter.reportStart(startProcessSource);

        try {
            // Apply source filter first. If source filtered out then delete source immediately and return.
            if (!sourceFilter.filterSource(source)) {
                this.sqsManager.deleteMessageFromQueue(source, ProgressState.deleteFilteredMessage);
                logger.debug("AWSCloudTrailSource " + source + " has filtered.");
                processSourceSuccess = true;

            } else {
                int nLogFilesToProcess = ((SQSBasedSource)source).getLogs().size();

                for (CloudTrailLog ctLog : ((SQSBasedSource)source).getLogs()) {
                    //start to process the log
                    boolean processLogSuccess = false;
                    ProgressStatus startProcessLog = new ProgressStatus(ProgressState.processLog, new BasicProcessLogInfo(source, ctLog, processLogSuccess));
                    final Object processLogReportObject = this.progressReporter.reportStart(startProcessLog);

                    try {
                        byte[] s3ObjectBytes = this.s3Manager.downloadLog(ctLog, source);
                        if (s3ObjectBytes == null) {
                            continue; //download log file fail, skip it.
                        }

                        try (GZIPInputStream gzippedInputStream = new GZIPInputStream(new ByteArrayInputStream(s3ObjectBytes));
                            AWSCloudTrailRecordSerializer serializer = this.getRecordSerializer(gzippedInputStream, ctLog);) {

                            this.emitRecords(serializer);

                            //decrement this value upon successfully processed a log
                            nLogFilesToProcess --;
                            processLogSuccess = true;

                        } catch (IllegalArgumentException | IOException e) {
                            ClientLibraryException exception = new ClientLibraryException("Fail to parse log file.", e, startProcessLog);
                            this.exceptionHandler.handleException(exception);
                        }
                    } finally {
                        //end to process the log
                        ProgressStatus endProcessLog = new ProgressStatus(ProgressState.processLog, new BasicProcessLogInfo(source, ctLog, processLogSuccess));
                        this.progressReporter.reportEnd(endProcessLog, processLogReportObject);
                    }
                }

                // Delete source after all log files processed successfully
                if (nLogFilesToProcess == 0) {
                    this.sqsManager.deleteMessageFromQueue(source, ProgressState.deleteMessage);
                    processSourceSuccess = true;
                }
            }

        } catch (CallbackException ex) {
            this.exceptionHandler.handleException(ex);
        } finally {
            // end to process the source
            ProgressStatus endProcessSource = new ProgressStatus(ProgressState.processSource, new BasicProcessSourceInfo(source, processSourceSuccess));
            this.progressReporter.reportEnd(endProcessSource, processSourceReportObject);
        }
    }

    /**
     * Get the AWSCloudTrailRecordSerializer based on user's configuration.
     *
     * @param inputStream the Gzipped content from CloudTrail log file
     * @param ctLog CloudTrail log file
     * @return parser that parse CloudTrail log file
     * @throws IOException
     */
    private AWSCloudTrailRecordSerializer getRecordSerializer(GZIPInputStream inputStream, CloudTrailLog ctLog) throws IOException {
        AWSCloudTrailRecordSerializer serializer;

        if (this.config.isEnableRawRecordInfo()) {
            String logFileContent = new String(ClientLibraryUtils.toByteArray(inputStream));
            JsonParser jsonParser = this.mapper.getFactory().createJsonParser(logFileContent);
            serializer = new RawLogDeliveryRecordSerializer(logFileContent, ctLog, jsonParser);
        } else {
            JsonParser jsonParser = this.mapper.getFactory().createParser(inputStream);
            serializer = new DefaultRecordSerializer(ctLog, jsonParser);
        }
        return serializer;
    }

    /**
     * Filter, buffer and emit ClientTrailClientRecods.
     *
     * @param serializer parser that parse CloudTrail log file
     * @throws IOException
     * @throws CallbackException
     */
    private void emitRecords(AWSCloudTrailRecordSerializer serializer) throws IOException, CallbackException {
        RecordBuffer<CloudTrailClientRecord> recordBuffer = new RecordBuffer<>(this.config.getNRecordsPerEmit());
        while (serializer.hasNextRecord()) {

            CloudTrailClientRecord clientRecord = serializer.getNextRecord();

            if (this.recordFilter.filterRecord(clientRecord)) {
                recordBuffer.addRecord(clientRecord);

                if (recordBuffer.isBufferFull()) {
                    this.recordsProcessor.process(recordBuffer.getRecords());
                }

            } else {
                logger.debug("AWSCloudTrailClientRecord " + clientRecord + " has filtered.");
            }
        }

        //emit whatever in the buffer as last batch
        List<CloudTrailClientRecord> records = recordBuffer.getRecords();
        if (!records.isEmpty()) {
            this.recordsProcessor.process(records);
        }
    }
}
