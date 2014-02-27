package com.amazonaws.services.cloudtrail.clientlibrary.exceptions;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;


public class LogDownloadingException extends ClientLibraryException {

	private static final long serialVersionUID = -3509155118602427237L;
	
	private CloudTrailLog cloudTrailLog;
	
	public LogDownloadingException(String message, CloudTrailLog cloudTrailLog) {
		super(message);
	}
	
	public LogDownloadingException(String message, Exception e, CloudTrailLog cloudTrailLog) {
		super(message, e);
		this.cloudTrailLog = cloudTrailLog;
	}

	/**
	 * @return the source
	 */
	public CloudTrailLog getSource() {
		return cloudTrailLog;
	}

}
