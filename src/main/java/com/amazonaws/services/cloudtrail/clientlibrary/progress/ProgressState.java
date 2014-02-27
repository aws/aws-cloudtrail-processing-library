package com.amazonaws.services.cloudtrail.clientlibrary.progress;

public enum ProgressState {
	pollQueue,
	processSource,
	downloadLog,
	processLog
}
