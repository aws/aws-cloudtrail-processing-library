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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.LogDownloadingException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.LogParsingException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.MessageParsingException;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.clientlibrary.model.ClientRecord;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.DownloadLogInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.PollMessageInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProcessLogInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProcessSourceInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.clientlibrary.serializer.AWSCloudTrailJacksonSerializer;
import com.amazonaws.services.cloudtrail.clientlibrary.signing.SignatureVerifier;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.RecordBuffer;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.S3Manager;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.SqsManager;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * AWSCloudTrailRecordReader is responsible to 
 */
public class AWSCloudTrailRecordReader {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailRecordReader.class);

    private final SourceFilter sourceFilter;
    private final RecordFilter recordFilter;
    private final RecordsProcessor recordsProcessor;
    private final ProgressReporter progressReporter;
    private final ExceptionHandler exceptionHandler;
    
    private AWSCloudTrailClientConfiguration config;
    
    private SqsManager sqsManager;
	private S3Manager s3Manager;
	    
    /**
     * This constructor creates an instance of AWSCloudTrailRecordReader object. For each user defined 
     * process unit, we provide a default implementation in case user doesn't specify it. The default 
     * filters are all pass filters that return true for any input; the default emitter is no-op function.
     * 
     * AWSCloudTrailClientLibConfiguration parameter cannot be null.
     *  
     * @param configuration 
     * @param metrics 
     * @param args
     */
    protected AWSCloudTrailRecordReader(RecordsProcessor recordsProcesor, SourceFilter sourceFilter, RecordFilter recordFilter, 
    		ProgressReporter progressReporter, ExceptionHandler exceptionHandler, SqsManager sqsManager, 
    		S3Manager s3Manager, AWSCloudTrailClientConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration is null when init AWSCloudTrailRecordReader object");
        }
        this.config = configuration;
        
        this.recordsProcessor = recordsProcesor;
        this.sourceFilter = sourceFilter;
        this.recordFilter = recordFilter;
        this.progressReporter = progressReporter;
        this.exceptionHandler = exceptionHandler;
        
        this.sqsManager = sqsManager;
        this.s3Manager = s3Manager;

    }
    
    /**
     * Poll messages from SQS Queue and apply sourceFilter
     * 
     * @return
     */
    public List<CloudTrailSource> getFilteredSources() {
    	final Object reportObject = this.progressReporter.reportStart(new ProgressStatus(ProgressState.pollQueue, new PollMessageInfo(0)), this.config);
    	
    	List<CloudTrailSource> filteredSources = new ArrayList<CloudTrailSource>();
        
    	List<CloudTrailSource> sources = this.sqsManager.pollQueue();
        
        for (CloudTrailSource source : sources) { 
        	
            if (sourceFilter.filterSource(source, this.config)) {
            	filteredSources.add(source);
            } else {
            	logger.debug("AWSCloudTrailSource " + source + " has filtered.");
            }
        }

    	this.progressReporter.reportEnd(new ProgressStatus(ProgressState.pollQueue, new PollMessageInfo(sources.size())), reportObject, this.config);
        return filteredSources;
    }
    
    /**
     * High level API that pull S3 object URL from source then download the object process each record through
     * call back functions. High Level API does not return a list of processed AWSCloudTrailRecord.
     * 
     * @param source
     * @return A list of CloudTrailRecord in source
     * @throws IOException 
     * @throws ClientLibraryException 
     * @throws MessageParsingException 
     */
    public void processSource (CloudTrailSource source) {
    	// start to process the source
    	final Object processSourceReportObject = this.progressReporter.reportStart(new ProgressStatus(ProgressState.processSource, new ProcessSourceInfo(source)), this.config);
    	
    	boolean deleteMessage = true;
    	
    	for (CloudTrailLog ctLog : source.getLogs()) {
    		//start to process the log
        	final Object processLogReportObject = this.progressReporter.reportStart(new ProgressStatus(ProgressState.processLog, new ProcessLogInfo(source, ctLog)), this.config);

	    	byte[] s3ObjectBytes = downloadAndValidateCloudTrailLog(ctLog, source);
			
			try (GZIPInputStream gzippedInputStream = new GZIPInputStream(new ByteArrayInputStream(s3ObjectBytes));
					AWSCloudTrailJacksonSerializer serializer = new AWSCloudTrailJacksonSerializer(gzippedInputStream, ctLog);) {
	            
				RecordBuffer<ClientRecord> rb = new RecordBuffer<>(this.config.recordBufferSize);
	            while (serializer.hasNextRecord()) {
	
	            	ClientRecord clientRecord = serializer.getNextRecord();
	            	
	                if (clientRecord == null) {
	                    continue;
	                }
	                
	                if (this.recordFilter.filterRecord(clientRecord, this.config)) {
	            		rb.addRecord(clientRecord);
	
	                	if (rb.isBufferFull()) {
	            			this.recordsProcessor.process(rb.getRecords(), this.config);
	                	}
	                	
	                } else {
	                	logger.debug("AWSCloudTrailClientRecord " + clientRecord + " has filtered.");
	                }
	            }
	            
	            //emit whatever in the buffer as last batch
	            List<ClientRecord> records = rb.getRecords();
	            if (!records.isEmpty()) {
	            	this.recordsProcessor.process(records, this.config);
	            }
	            
			} catch (IOException e) {
	    		ClientLibraryException exception = new LogParsingException("Fail to parse log file.", e, ctLog);
	    		this.exceptionHandler.handleException(exception, this.config);
	    		deleteMessage = false;
			}
			
			//end to process the log
			this.progressReporter.reportEnd(new ProgressStatus(ProgressState.processLog, new ProcessLogInfo(source, ctLog)), processLogReportObject, this.config);
    	}
		
    	if (deleteMessage) {
    		//TODO: enable it before release
//    		this.sqsManager.deleteMessageFromQueue(batch);
    	}
    	
    	// end to process the source
		this.progressReporter.reportEnd(new ProgressStatus(ProgressState.processSource, new ProcessSourceInfo(source)), processSourceReportObject, this.config);
    }
    
    /**
     * A helper function that download source and validate source's signature when needed.
     * 
     * @param ctLog
     * @return
     */
    private byte[] downloadAndValidateCloudTrailLog(CloudTrailLog ctLog, CloudTrailSource source) {
		// start to download CloudTrail log
    	final Object downloadSourceReportObject = this.progressReporter.reportStart(new ProgressStatus(ProgressState.downloadLog, new DownloadLogInfo(source, ctLog)), this.config);
    	
    	byte[] s3ObjectBytes = null;
    	S3Object s3Object = this.s3Manager.getObject(ctLog.getS3Bucket(), ctLog.getS3ObjectKey());
    	
    	try (S3ObjectInputStream s3InputStream = s3Object.getObjectContent()){
    		s3ObjectBytes = ClientLibraryUtils.toByteArray(s3InputStream);
    	} catch (IOException e) {
    		ClientLibraryException exception = new LogDownloadingException("Fail to parse log file.", e, ctLog);
    		this.exceptionHandler.handleException(exception, this.config);
    	}	
    	
    	// end to download CloudTrail log
    	this.progressReporter.reportEnd(new ProgressStatus(ProgressState.downloadLog, new DownloadLogInfo(source, ctLog)), downloadSourceReportObject, this.config);
		
		// verify CloudTrail log file signature when enabled
		if (this.config.verifyCloudTrailLogFile) {
			SignatureVerifier.verifyLogFile(s3ObjectBytes, s3Object.getObjectMetadata().getUserMetadata(), ctLog, this.s3Manager);
		}
		
		return s3ObjectBytes;
    }
    
    
    /**
     * Get an instance of SqsManager
     * @return
     */
    public SqsManager getSqsManager() {
		return sqsManager;
	}
}
