package com.amazonaws.services.cloudtrail.clientlibrary.exceptions;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;

public class LogParsingException extends ClientLibraryException {

	private static final long serialVersionUID = 3272911742630360836L;
	private CloudTrailLog source;
	
	public LogParsingException(String message, CloudTrailLog source) {
		super(message);
		this.source = source;
	}
	
	public LogParsingException(String message, Exception e, CloudTrailLog source) {
		super(message, e);
		this.source = source;
	}
	
	/**
	 * @return the source
	 */
	public CloudTrailLog getSource() {
		return source;
	}
}
