package com.amazonaws.services.cloudtrail.clientlibrary.progress;

public class PollMessageInfo extends ProgressInfo {
	private int messagePolledCount;
	
	public PollMessageInfo(int messagePolledCount) {
		this.messagePolledCount = messagePolledCount;
	}

	/**
	 * @return the messagePolledCount
	 */
	public int getMessagePolledCount() {
		return messagePolledCount;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PollMessageInfo [messagePolledCount=")
				.append(messagePolledCount).append("]");
		return builder.toString();
	}

}
