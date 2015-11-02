/*******************************************************************************
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicParseMessageInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicPollQueueInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicProcessSourceInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.SourceSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.DefaultSourceSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private AmazonSQSClient sqsClient;

    /**
     * An instance of AWSCloudTrailSourceSeriazlier.
     */
    private SourceSerializer serializer;
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
     */
    public SqsManager(AmazonSQSClient sqsClient, ProcessingConfiguration config, ExceptionHandler exceptionHandler, ProgressReporter progressReporter) {
        this.config = config;
        this.exceptionHandler = exceptionHandler;
        this.progressReporter = progressReporter;
        this.sqsClient = sqsClient;
        this.serializer = new DefaultSourceSerializer(new ObjectMapper());

        this.validate();
    }

    /**
     * Poll SQS queue for incoming messages, filter them, and return a list of SQS Messages.
     *
     * @return a list of SQS messages.
     */
    public List<Message> pollQueue() {
        boolean success = false;
        ProgressStatus startStatus = new ProgressStatus(ProgressState.pollQueue, new BasicPollQueueInfo(0, success));
        final Object reportObject = this.progressReporter.reportStart(startStatus);

        ReceiveMessageRequest request = new ReceiveMessageRequest().withAttributeNames(ALL_ATTRIBUTES);
        request.setQueueUrl(this.config.getSqsUrl());
        request.setVisibilityTimeout(this.config.getVisibilityTimeout());
        request.setMaxNumberOfMessages(DEFAULT_SQS_MESSAGE_SIZE_LIMIT);
        request.setWaitTimeSeconds(DEFAULT_WAIT_TIME_SECONDS);

        List<Message> sqsMessages = new ArrayList<Message>();
        try {

            ReceiveMessageResult result = sqsClient.receiveMessage(request);
            sqsMessages = result.getMessages();
            logger.info("Polled " + sqsMessages.size() + " sqs messages from " + this.config.getSqsUrl());

            success = true;
        } catch (AmazonServiceException e) {
            // delegate exception to ExceptionHandler
            ProcessingLibraryException exception = new ProcessingLibraryException("Failed to poll sqs message.", e, startStatus);
            this.exceptionHandler.handleException(exception);

        } finally {
            ProgressStatus endStatus = new ProgressStatus(ProgressState.pollQueue, new BasicPollQueueInfo(sqsMessages.size(), success));
            this.progressReporter.reportEnd(endStatus, reportObject);
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
            boolean success = false;
            ProgressStatus startStatus = new ProgressStatus(ProgressState.parseMessage, new BasicParseMessageInfo(sqsMessage, success));
            final Object reportObject = this.progressReporter.reportStart(startStatus);

            try {
                CloudTrailSource source = this.serializer.getSource(sqsMessage);
                sources.add(source);

                success = true;
            } catch (IOException e) {

                // delegate exception to ExceptionHandler
                ProcessingLibraryException exception = new ProcessingLibraryException("Failed to parse sqs message", e, startStatus);
                this.exceptionHandler.handleException(exception);

            } finally {
                ProgressStatus endStatus = new ProgressStatus(ProgressState.parseMessage, new BasicParseMessageInfo(sqsMessage, success));
                this.progressReporter.reportEnd(endStatus, reportObject);
            }
        }
        return sources;
    }

    /**
     * Delete a message from SQS queue, assume the message is coming from the queueName setup in configuration.
     *
     * @param source CloudTrailSource (SQSBasedSource) contains SQS message that need to be deleted.
     * @param state current running state.
     */
    public void deleteMessageFromQueue(CloudTrailSource source, ProgressState state) {
        boolean success = false;
        ProgressStatus startStatus = new ProgressStatus(state, new BasicProcessSourceInfo(source, success));
        final Object reportObject = this.progressReporter.reportStart(startStatus);

        try{
            this.sqsClient.deleteMessage(new DeleteMessageRequest(config.getSqsUrl(), ((SQSBasedSource)source).getSqsMessage().getReceiptHandle()));

            success = true;
        } catch (AmazonServiceException e){

            // delegate exception to ExceptionHandler
            ProcessingLibraryException exception = new ProcessingLibraryException("Failed to delete sqs message", e, startStatus);
            this.exceptionHandler.handleException(exception);

        } finally {
            ProgressStatus endStatus = new ProgressStatus(state, new BasicProcessSourceInfo(source, success));
            this.progressReporter.reportEnd(endStatus, reportObject);
        }
    }

    /**
     * Convenient function to validate input
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(this.config, "configuration is null");
        LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exception handler is null");
        LibraryUtils.checkArgumentNotNull(this.progressReporter, "progress reporter is null");
        LibraryUtils.checkArgumentNotNull(this.sqsClient, "sqs client is null");
        LibraryUtils.checkArgumentNotNull(this.serializer, "source serializer is null");
    }
}
