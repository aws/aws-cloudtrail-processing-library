package com.amazonaws.services.cloudtrail.clientlibrary.interfaces;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;

public interface ExceptionHandler {
	public void handleException(ClientLibraryException exception, AWSCloudTrailClientConfiguration config) ;
}
