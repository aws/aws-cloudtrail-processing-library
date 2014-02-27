package com.amazonaws.services.cloudtrail.clientlibrary.model;

import java.util.List;

/**
 * Each message poll from SQS will end up as a Source object. 
 *
 */
public class CloudTrailSource {
    /**
     * AWS Account ID
     */
    private final String accountId;
    
    /**
     * An identifier associated with the act of receiving a SQS message. When
     * deleting a SQS message, we provide the last received receipt handle to
     * delete the message.
     */
    private final String handle;
    
    private final List<CloudTrailLog> logs;

	public CloudTrailSource(String accountId, String handle, List<CloudTrailLog> logs) {
		this.accountId = accountId;
		this.handle = handle;
		this.logs = logs;
	}

	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @return the handle
	 */
	public String getHandle() {
		return handle;
	}

	/**
	 * @return the sources
	 */
	public List<CloudTrailLog> getLogs() {
		return logs;
	}
    
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "CloudTrailSource [accountId=" + accountId + ", handle=" + handle
				+ ", logs=" + logs + "]";
	}    
}
