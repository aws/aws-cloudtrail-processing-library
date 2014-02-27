package com.amazonaws.services.cloudtrail.clientlibrary.interfaces;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;

public interface ProgressReporter {
	public Object reportStart(ProgressStatus status, AWSCloudTrailClientConfiguration config);
	public void reportEnd(ProgressStatus status, Object object, AWSCloudTrailClientConfiguration config);
}
