package com.amazonaws.services.cloudtrail.clientlibrary.exceptions;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;

public class MessageDeletingException extends ClientLibraryException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7954948812488910132L;
	
	private CloudTrailSource batch;
	
	public MessageDeletingException(String message, CloudTrailSource batch) {
		super(message);
		this.batch = batch;
	}
	
	public MessageDeletingException(String message, Exception e, CloudTrailSource batch) {
		super(message, e);
		this.batch = batch;
	}

	/**
	 * @return the sqsMessage
	 */
	public CloudTrailSource getBatch() {
		return batch;
	}
}
