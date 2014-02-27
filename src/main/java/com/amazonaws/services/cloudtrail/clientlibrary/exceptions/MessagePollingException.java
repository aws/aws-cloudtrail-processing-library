package com.amazonaws.services.cloudtrail.clientlibrary.exceptions;


public class MessagePollingException extends ClientLibraryException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3953378087962814805L;

	
	public MessagePollingException(String message) {
		super(message);
	}
	
	public MessagePollingException(String message, Exception e) {
		super(message, e);
	}
}
