package com.amazonaws.services.cloudtrail.clientlibrary.progress;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;

public class DownloadLogInfo extends ProgressInfo {
	private CloudTrailSource source;
	private CloudTrailLog log;
	
	public DownloadLogInfo(CloudTrailSource source, CloudTrailLog log) {
		super();
		this.source = source;
		this.log = log;
	}

	/**
	 * @return the source
	 */
	public CloudTrailSource getSource() {
		return source;
	}

	/**
	 * @return the log
	 */
	public CloudTrailLog getLog() {
		return log;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "DownloadLogInfo [source=" + source + ", log=" + log + "]";
	}
	
}
