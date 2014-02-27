package com.amazonaws.services.cloudtrail.clientlibrary.progress;

public class ProgressStatus {
	
	private ProgressState state;
	private ProgressInfo statusInfo;
	
	public ProgressStatus(ProgressState state, ProgressInfo statusInfo) {
		this.state = state;
		this.statusInfo = statusInfo;
	}
	
	/**
	 * @return the state
	 */
	public ProgressState getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(ProgressState state) {
		this.state = state;
	}
	/**
	 * @return the statusInfo
	 */
	public ProgressInfo getStatusInfo() {
		return statusInfo;
	}
	/**
	 * @param statusInfo the statusInfo to set
	 */
	public void setStatusInfo(ProgressInfo statusInfo) {
		this.statusInfo = statusInfo;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return "ProgressStatus [state=" + state + ", statusInfo=" + statusInfo
				+ "]";
	}
	
}
