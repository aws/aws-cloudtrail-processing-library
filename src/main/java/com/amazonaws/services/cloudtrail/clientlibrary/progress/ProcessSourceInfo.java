package com.amazonaws.services.cloudtrail.clientlibrary.progress;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;

public class ProcessSourceInfo extends ProgressInfo{
	private CloudTrailSource source;

	public ProcessSourceInfo(CloudTrailSource source) {
		super();
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessSourceInfo [source=").append(source).append("]");
		return builder.toString();
	}

	/**
	 * @return the source
	 */
	public CloudTrailSource getSource() {
		return source;
	}

}
