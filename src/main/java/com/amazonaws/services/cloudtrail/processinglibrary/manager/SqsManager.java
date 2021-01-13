/*******************************************************************************
 * Copyright 2010-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 ******************************************************************************/

package com.amazonaws.services.cloudtrail.processinglibrary.manager;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SourceAttributeKeys;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.SourceType;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicParseMessageInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicPollQueueInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.SourceSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * A convenient class to manage Amazon SQS Service related operations.
 */
public class SqsManager {
    private static final Log logger = LogFactory.getLog(SqsManager.class);

    private static final String ALL_ATTRIBUTES = "All";

    /**
     * Pull 10 messages from SQS queue at a time.
     */
    private static final int DEFAULT_SQS_MESSAGE_SIZE_LIMIT = 10;

    /**
     * Enable long pulling, wait at most 20 seconds for a incoming messages for a single poll queue request.
     */
    private static final int DEFAULT_WAIT_TIME_SECONDS = 20;

    /**
     * An instance of ProcessingConfiguration.
     */
    private ProcessingConfiguration config;

    /**
     * An instance of AmazonSQSClient.
     */
    private AmazonSQS sqsClient;

    /**
     * An instance of SourceSerializer.
     */
    private SourceSerializer sourceSerializer;
    /**
     * User implementation of ExceptionHandler, used to handle error case.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * User implementation of ProgressReporter, used to report progress.
     */
    private ProgressReporter progressReporter;

    /**
     * SqsManager constructor.
     *
     * @param sqsClient used to poll message from SQS.
     * @param config user provided ProcessingConfiguration.
     * @param exceptionHandler user provided exceptionHandler.
     * @param progressReporter user provided progressReporter.
     * @param sourceSerializer user provided SourceSerializer.
     */
    public SqsManager(AmazonSQS sqsClient,
                      ProcessingConfiguration config,
                      ExceptionHandler exceptionHandler,
                      ProgressReporter progressReporter,
                      SourceSerializer sourceSerializer) {
        this.config = config;
        this.exceptionHandler = exceptionHandler;
        this.progressReporter = progressReporter;
        this.sqsClient = sqsClient;
        this.sourceSerializer = sourceSerializer;

        validate();
    }

    /**
     * Poll SQS queue for incoming messages, filter them, and return a list of SQS Messages.
     *
     * @return a list of SQS messages.
     */
    public List<Message> pollQueue() {
        boolean success = false;
        ProgressStatus pollQueueStatus = new ProgressStatus(ProgressState.pollQueue, new BasicPollQueueInfo(0, success));
        final Object reportObject = progressReporter.reportStart(pollQueueStatus);

        ReceiveMessageRequest request = new ReceiveMessageRequest().withAttributeNames(ALL_ATTRIBUTES);
        request.setQueueUrl(config.getSqsUrl());
        request.setVisibilityTimeout(config.getVisibilityTimeout());
        request.setMaxNumberOfMessages(DEFAULT_SQS_MESSAGE_SIZE_LIMIT);
        request.setWaitTimeSeconds(DEFAULT_WAIT_TIME_SECONDS);

        List<Message> sqsMessages = new ArrayList<Message>();
        try {

            ReceiveMessageResult result = sqsClient.receiveMessage(request);
            sqsMessages = result.getMessages();
            logger.info("Polled " + sqsMessages.size() + " sqs messages from " + config.getSqsUrl());

            success = true;
        } catch (AmazonServiceException e) {
            LibraryUtils.handleException(exceptionHandler, pollQueueStatus, e, "Failed to poll sqs message.");

        } finally {
            LibraryUtils.endToProcess(progressReporter, success, pollQueueStatus, reportObject);
        }
        return sqsMessages;

    }

    /**
     * Given a list of raw SQS message parse each of them, and return a list of CloudTrailSource.
     *
     * @param sqsMessages list of SQS messages.
     * @return list of CloudTrailSource.
     */
    public List<CloudTrailSource> parseMessage(List<Message> sqsMessages) {

        List<CloudTrailSource> sources = new ArrayList<>();

        for (Message sqsMessage : sqsMessages) {
            boolean parseMessageSuccess = false;
            ProgressStatus parseMessageStatus = new ProgressStatus(ProgressState.parseMessage, new BasicParseMessageInfo(sqsMessage, parseMessageSuccess));
            final Object reportObject = progressReporter.reportStart(parseMessageStatus);
            CloudTrailSource ctSource = null;

            try {
                ctSource = sourceSerializer.getSource(sqsMessage);

                if (containsCloudTrailLogs(ctSource)) {
                    sources.add(ctSource);
                    parseMessageSuccess = true;
                }
            } catch (Exception e) {
                LibraryUtils.handleException(exceptionHandler, parseMessageStatus, e, "Failed to parse sqs message.");

            } finally {
                if (containsCloudTrailValidationMessage(ctSource) || shouldDeleteMessageUponFailure(parseMessageSuccess)) {
                    deleteMessageFromQueue(sqsMessage, new ProgressStatus(ProgressState.deleteMessage, new BasicParseMessageInfo(sqsMessage, false)));
                }
                LibraryUtils.endToProcess(progressReporter, parseMessageSuccess, parseMessageStatus, reportObject);
            }
        }
        return sources;
    }

    /**
     * Delete a message from the SQS queue that you specified in the configuration file.
     *
     * @param sqsMessage the {@link Message} that you want to delete.
     * @param progressStatus {@link ProgressStatus} tracks the start and end status.
     *
     */
    public void deleteMessageFromQueue(Message sqsMessage, ProgressStatus progressStatus) {
        final Object reportObject = progressReporter.reportStart(progressStatus);
        boolean deleteMessageSuccess = false;
        try {
            sqsClient.deleteMessage(new DeleteMessageRequest(config.getSqsUrl(), sqsMessage.getReceiptHandle()));
            deleteMessageSuccess = true;
        } catch (AmazonServiceException e) {
            LibraryUtils.handleException(exceptionHandler, progressStatus, e, "Failed to delete sqs message.");
        }
        LibraryUtils.endToProcess(progressReporter, deleteMessageSuccess, progressStatus, reportObject);
    }

    /**
     * Check whether <code>ctSource</code> contains CloudTrail log files.
     * @param ctSource a {@link CloudTrailSource}.
     * @return <code>true</code> if contains CloudTrail log files, <code>false</code> otherwise.
     *
     */
    private boolean containsCloudTrailLogs(CloudTrailSource ctSource) {
        SourceType sourceType = SourceType.valueOf(ctSource.getSourceAttributes().get(SourceAttributeKeys.SOURCE_TYPE.getAttributeKey()));
        switch(sourceType) {
            case CloudTrailLog:
                return true;
            case CloudTrailValidationMessage:
                logger.warn(String.format("Delete CloudTrail validation message: %s.", ctSource.toString()));
                return false;
            case Other:
            default:
                logger.info(String.format("Skip Non CloudTrail Log File: %s.", ctSource.toString()));
                return false;
        }
    }

    /**
     * Check whether <code>ctSource</code> contains CloudTrail validation message.
     * @param ctSource a {@link CloudTrailSource}.
     * @return <code>true</code> if contains CloudTrail validation message, <code>false</code> otherwise.
     *
     */
    private boolean containsCloudTrailValidationMessage(CloudTrailSource ctSource) {
        if (ctSource == null){
           return false;
        }

        SourceType sourceType = SourceType.valueOf(ctSource.getSourceAttributes().get(SourceAttributeKeys.SOURCE_TYPE.getAttributeKey()));
        return sourceType == SourceType.CloudTrailValidationMessage;
    }

    /**
     * Delete the message if the CPL failed to process the message and {@link ProcessingConfiguration#isDeleteMessageUponFailure()}
     * is enabled.
     * @param processSuccess Indicates whether the CPL processing is successful, such as parsing message, or
     *                       consuming the events in the CloudTrail log file.
     * @return <code>true</code> if the message is removable. Otherwise, <code>false</code>.
     */
    public boolean shouldDeleteMessageUponFailure(boolean processSuccess) {
        return !processSuccess && config.isDeleteMessageUponFailure();
    }

    /**
     * Convenient function to validate input.
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(config, "configuration is null");
        LibraryUtils.checkArgumentNotNull(exceptionHandler, "exceptionHandler is null");
        LibraryUtils.checkArgumentNotNull(progressReporter, "progressReporter is null");
        LibraryUtils.checkArgumentNotNull(sqsClient, "sqsClient is null");
        LibraryUtils.checkArgumentNotNull(sourceSerializer, "sourceSerializer is null");
    }
}
