package com.amazonaws.services.cloudtrail.clientlibrary.progress;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;

public class ProcessLogInfo extends ProgressInfo {
	private CloudTrailSource source;
	private CloudTrailLog log;
	
	public ProcessLogInfo(CloudTrailSource source, CloudTrailLog log) {
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
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessLogInfo [source=");
		builder.append(source);
		builder.append(", log=");
		builder.append(log);
		builder.append("]");
		return builder.toString();
	}
}
