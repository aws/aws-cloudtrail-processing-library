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
package com.amazonaws.services.cloudtrail.clientlibrary.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.MessageDeletingException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.MessageParsingException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.MessagePollingException;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A convenient class to manage Amazon SQS Service related operations.
 */
public class SqsManager {
	private static final Log logger = LogFactory.getLog(SqsManager.class);

	private static final String CLOUD_TRAIL_SUFFIX = "/CloudTrail";
	private static final String AWS_LOGS_PREFIX = "AWSLogs/";
	
	/**
	 * Pull 10 messages from SQS queue at time.
	 */
	private static final int DEFAULT_SQS_MESSAGE_SIZE_LIMIT = 10;
	
	/**
	 * Enable long pulling, wait 20 seconds for incoming messages to reach DEFAULT_SQS_MESSAGE_SIZE_LIMIT limit
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
     * An instance of ObjectMapper
     */
    private ObjectMapper mapper;
    
    private ExceptionHandler exceptionHandler;
    
    /**
     * SqsManager constructor 
     * @param config
     */
    public SqsManager(AWSCloudTrailClientConfiguration config, ExceptionHandler exceptionHandler) {
        this.config = config;
        this.exceptionHandler = exceptionHandler;
        
        this.sqsClient = new AmazonSQSClient(config.awsCredentialsProvider);
        this.sqsClient.setRegion(Region.getRegion(Regions.fromName(this.config.sqsRegion)));
        
        this.mapper = new ObjectMapper();
    }
    
    /**
     * Poll SQS queue for incoming messages, filter them, and return a list of AWSCloudTrailMessage.
     * 
     * @return
     */
    public List<CloudTrailSource> pollQueue() {
        
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setMaxNumberOfMessages(DEFAULT_SQS_MESSAGE_SIZE_LIMIT);
        request.setQueueUrl(this.config.sqsUrl);
        request.setVisibilityTimeout(this.config.visibilityTimeout);
        request.setWaitTimeSeconds(DEFAULT_WAIT_TIME_SECONDS);
        
        try {
        	
        	ReceiveMessageResult result = sqsClient.receiveMessage(request);
        	List<Message> sqsMessages = result.getMessages();
        	logger.info("Polled " + sqsMessages.size() + " sqs messages from " + this.config.sqsUrl);
        	return this.transform(sqsMessages);

        } catch (AmazonServiceException e) {
        	ClientLibraryException exception = new MessagePollingException("Failed to pool sqs message", e);
        	this.exceptionHandler.handleException(exception, this.config);
        }
        
        return null;
    }
    
    /**
     * Delete a message from SQS queue, assume the message is coming from the queueName setup in configuration.
     * 
     * @param message
     */
    public void deleteMessageFromQueue(CloudTrailSource batch) {
        try{
            this.sqsClient.deleteMessage(new DeleteMessageRequest(config.sqsUrl, batch.getHandle()));
        } catch (AmazonServiceException e){
        	ClientLibraryException exception = new MessageDeletingException("Failed to pool sqs message", e, batch);
        	this.exceptionHandler.handleException(exception, this.config);
        }
    }
    
    /**
     * Given a list of raw SQS message transform it to a list of AWSCloudTrailMessage
     * 
     * @param sqsMessages
     * @return
     */
    private List<CloudTrailSource> transform(List<Message> sqsMessages) {
        List<CloudTrailSource> sources = new ArrayList<>();
        
        for (Message sqsMessage : sqsMessages) {
        	List<CloudTrailLog> cloudTrailLogs = new ArrayList<>();
            String handle = sqsMessage.getReceiptHandle();
            String accountId = null;
            
            try {
                String messageText = this.mapper.readTree(sqsMessage.getBody()).get("Message").textValue();
                JsonNode messageNode = this.mapper.readTree(messageText);
                
                String bucketName = messageNode.path("s3Bucket").textValue();
                List<String> objectKeys = this.mapper.readValue(messageNode.get("s3ObjectKey").traverse(), new TypeReference<List<String>>(){});
                
                for (String objectKey : objectKeys) {
                    accountId = accountId == null ? this.extractAccountId(objectKey.toString()) : accountId;

                    cloudTrailLogs.add(new CloudTrailLog(bucketName, objectKey));
                }
                
                sources.add(new CloudTrailSource(accountId, handle, cloudTrailLogs));
                
            } catch (IOException e) {
            	// delegate exception to ExceptionHandler, continue next Message after 
            	ClientLibraryException exception = new MessageParsingException("Failed to parse sqs message", e, sqsMessage);
            	this.exceptionHandler.handleException(exception, this.config);
            }
        }
        
        return sources;
    }
    
    /**
     * S3 object key contains account Id, extract it.
     * 
     * @param objectKey
     * @return
     */
    private String extractAccountId(String objectKey) {
        if (objectKey == null) {
            return null;
        }

        int start = objectKey.indexOf(AWS_LOGS_PREFIX);

        if (start != -1) {
            int end = objectKey.indexOf(CLOUD_TRAIL_SUFFIX, start + AWS_LOGS_PREFIX.length());

            if (end != -1) {
                return objectKey.substring(start + AWS_LOGS_PREFIX.length(), end);
            }
        }
        return null;
    }
}
