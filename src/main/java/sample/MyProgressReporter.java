package sample;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;

public class MyProgressReporter implements ProgressReporter {
	private static final Log logger = LogFactory.getLog(DefaultProgressReporter.class);

	@Override
	public Object reportStart(ProgressStatus status, AWSCloudTrailClientConfiguration config) {
		logger.info(status.getState().toString());
		return new Date();
	}

	@Override
	public void reportEnd(ProgressStatus status, Object object, AWSCloudTrailClientConfiguration config) {
		logger.info("Status Info: " + status.getStatusInfo());
		logger.info(status.getState().toString() + ", and latency is " + Math.abs(((Date) object).getTime()-new Date().getTime()) + " milliseconds.");
	}
}
