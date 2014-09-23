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
package com.amazonaws.services.cloudtrail.clientlibrary.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.clientlibrary.configuration.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.clientlibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.BasicParseMessageInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.BasicPollQueueInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.BasicProcessSourceInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.clientlibrary.serializer.AWSCloudTrailSourceSerializer;
import com.amazonaws.services.cloudtrail.clientlibrary.serializer.DefaultSourceSerializer;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A convenient class to manage Amazon SQS Service related operations.
 */
public class AWSCloudTrailClientSqsManager {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailClientSqsManager.class);

    private static final String ALL_ATTRIBUTES = "All";

    /**
     * Pull 10 messages from SQS queue at a time.
     */
    private static final int DEFAULT_SQS_MESSAGE_SIZE_LIMIT = 10;

    /**
     * Enable long pulling, wait at most 20 seconds for a incoming messages for a single poll queue request
     */
    private static final int DEFAULT_WAIT_TIME_SECONDS = 20;

    /**
     * An instance of AWSCloudTrailClientConfiguration
     */
    private AWSCloudTrailClientConfiguration config;

    /**
     * An instance of AmazonSQSClient
     */
    private AmazonSQSClient sqsClient;

    /**
     * An instance of AWSCloudTrailSourceSeriazlier
     */
    private AWSCloudTrailSourceSerializer serializer;
    /**
     * User implementation of ExceptionHandler, used to handle error case.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * User implementation of ProgressReporter, used to report progress.
     */
    private ProgressReporter progressReporter;

    /**
     * AWSCloudTrailClientSqsManager constructor
     *
     * @param config
     */
    public AWSCloudTrailClientSqsManager(AmazonSQSClient sqsClient, AWSCloudTrailClientConfiguration config, ExceptionHandler exceptionHandler, ProgressReporter progressReporter) {
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
     * @return
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
            ClientLibraryException exception = new ClientLibraryException("Failed to poll sqs message.", e, startStatus);
            this.exceptionHandler.handleException(exception);

        } finally {
            ProgressStatus endStatus = new ProgressStatus(ProgressState.pollQueue, new BasicPollQueueInfo(sqsMessages.size(), success));
            this.progressReporter.reportEnd(endStatus, reportObject);
        }
        return sqsMessages;

    }

    /**
     * Given a list of raw SQS message parse each of them, and return a list of CloudTrailSource
     *
     * @param sqsMessages
     * @return
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
                ClientLibraryException exception = new ClientLibraryException("Failed to parse sqs message", e, startStatus);
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
     * @param message
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
            ClientLibraryException exception = new ClientLibraryException("Failed to delete sqs message", e, startStatus);
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
        ClientLibraryUtils.checkArgumentNotNull(this.config, "configuration is null");
        ClientLibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exception handler is null");
        ClientLibraryUtils.checkArgumentNotNull(this.progressReporter, "progress reporter is null");
        ClientLibraryUtils.checkArgumentNotNull(this.sqsClient, "sqs client is null");
        ClientLibraryUtils.checkArgumentNotNull(this.serializer, "source serializer is null");
    }
}
