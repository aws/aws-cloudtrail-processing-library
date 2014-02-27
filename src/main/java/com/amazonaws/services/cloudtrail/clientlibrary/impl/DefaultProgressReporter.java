package com.amazonaws.services.cloudtrail.clientlibrary.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;

public class DefaultProgressReporter implements ProgressReporter {
	private static final Log logger = LogFactory.getLog(DefaultProgressReporter.class);

	@Override
	public Object reportStart(ProgressStatus status, AWSCloudTrailClientConfiguration config) {
		logger.info(status.getState().toString());
		return null;
	}

	@Override
	public void reportEnd(ProgressStatus status, Object object, AWSCloudTrailClientConfiguration config) {
		logger.info(status.getState().toString());
	}

}
