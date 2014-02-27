package com.amazonaws.services.cloudtrail.clientlibrary.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.MessageDeletingException;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.MessageParsingException;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.sqs.AmazonSQSClient;

public class DefaultExceptionHandler implements ExceptionHandler {
	private static final Log logger = LogFactory.getLog(DefaultProgressReporter.class);

	@Override
	public void handleException(ClientLibraryException exception, AWSCloudTrailClientConfiguration config) {
		logger.error(exception.getMessage(), exception);
		
		AmazonSQSClient sqs = new AmazonSQSClient(config.awsCredentialsProvider);
		sqs.setRegion(Region.getRegion(Regions.fromName(config.sqsRegion)));
		String receiptHandle = null;
		
		if (exception instanceof MessageParsingException) {
			receiptHandle = ((MessageParsingException) exception).getSqsMessage().getReceiptHandle();
		} else if (exception instanceof MessageDeletingException) {
			receiptHandle = ((MessageDeletingException) exception).getBatch().getHandle();
		} 
		
		if (receiptHandle != null) {
			sqs.deleteMessage(config.sqsUrl, receiptHandle);
		}
	}

}
