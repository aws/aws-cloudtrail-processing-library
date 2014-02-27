/*******************************************************************************
 * Copyright (c) 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.amazonaws.services.cloudtrail.clientlibrary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultRecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultRecordsEmitter;
import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultSourceFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.S3Manager;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.SqsManager;


/**
 * This class creates AWSCloudTrailRecordReader objects. It encapsulates maintains 
 * instance of the objects that AWSCloudTrailRecordReader will use to limit the 
 * parameters that we needed to create an instance.
 */
public class AWSCloudTrailRecordReaderFactory {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailRecordReaderFactory.class);

    /**
     * In instance of AWSCloudTrailClientConfiguration.
     */
    private AWSCloudTrailClientConfiguration config;
    
    /**
     * User's implementation of RecordsEmitter.
     */
    private RecordsProcessor recordsEmitter;
    
    /**
     * User's implementation of SourceFilter.
     */
    private SourceFilter sourceFilter;
    
    /**
     * User's implementation of RecordFilter.
     */
    private RecordFilter recordFilter;
    
    /**
     * User's implementation of ProgressReporter.
     */
    private ProgressReporter progressReporter;
    
    /**
     * User's implementation of ExceptionHandler.
     */
    private ExceptionHandler exceptionHander;

    private SqsManager sqsManager;
    
    private S3Manager s3Manager;
    
    /**
     * AWSCloudTrailRecordReaderFactory constructor, except AWSCloudTrailClientConfiguration 
     * other parameters can be null. 
     * 
	 * @param recordsEmitter
	 * @param sourceFilter
	 * @param recordFilter
	 * @param progressReporter
	 * @param exceptionHander
	 * @param config
	 */
	public AWSCloudTrailRecordReaderFactory(RecordsProcessor recordsEmitter, SourceFilter sourceFilter, 
			RecordFilter recordFilter, ProgressReporter progressReporter, ExceptionHandler exceptionHander, 
			AWSCloudTrailClientConfiguration config) {

		this.sourceFilter = sourceFilter;
		if (this.sourceFilter == null) {
			logger.info("sourceFilter is null use default source filter");
			this.sourceFilter = new DefaultSourceFilter();
		}

		this.recordFilter = recordFilter;
		if (this.recordFilter == null) {
			logger.info("eventFilter is null use default event filter");
			this.recordFilter = new DefaultRecordFilter();
		}

		this.recordsEmitter = recordsEmitter;
		//should not be null, since we have a check AWSCloudTrailClientExecutor, check here for double guard.
		if (this.recordsEmitter == null) {
			logger.info("emitter is null use default emitter");
			this.recordsEmitter = new DefaultRecordsEmitter();
		}

		this.progressReporter = progressReporter;
		if (this.progressReporter == null) {
			logger.info("metricsEmitter is null use default metrics filter");
			this.progressReporter = new DefaultProgressReporter();
		}
		
		this.exceptionHander = exceptionHander;
		if (this.exceptionHander == null) {
			logger.info("metricsEmitter is null use default metrics filter");
			this.exceptionHander = new DefaultExceptionHandler();
		}
		
		this.config = config;
		
		this.sqsManager = new SqsManager(this.config, this.exceptionHander);
		this.s3Manager= new S3Manager(this.config);

		this.validate();
	}

	/**
     * Create an instance of AWSCloudTrailRecordReader object.
	 * @param metrics 
     * @return
     */
    public AWSCloudTrailRecordReader createReader() {
		AWSCloudTrailRecordReader reader = new AWSCloudTrailRecordReader(
				this.recordsEmitter, this.sourceFilter, this.recordFilter, this.progressReporter, this.exceptionHander, 
		        this.sqsManager, this.s3Manager, this.config);
		return reader;
    }
    
    /**
     * Convenient function to validate input
     */
    private void validate() {
    	if (this.config == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": configuration is null");
    	}
    	
    	if (this.recordsEmitter == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": recordsEmitter is null");
    	}
    	
    	if (this.sourceFilter == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": sourceFilter is null");
    	}
    	
    	if (this.recordFilter == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": recordFilter is null");
    	}
    	
    	if (this.progressReporter == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": progressReporter is null");
    	}
    	
    	if (this.exceptionHander == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": exceptionHander is null");
    	}
    	
    	if (this.sqsManager == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": sqsManager is null");
    	}
    	
    	if (this.s3Manager == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": s3Manager is null");
    	}
    }
}
