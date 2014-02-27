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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;


/**
 * AWSCloudTrail client library's main execution logic. It loads user's configuration and create a 
 * AWSCloudTrailRecordReaderFactory object to spawner record reader to process log files. It has 
 * two thread pool. First one is scheduledThreadPool which create a single threaded scheduled thread
 * pool. It will periodically pool SQS queue and process records. Second thread pool has a configurable
 * size. It process each AWSCloudTrailSource in parallel.
 */
public class AWSCloudTrailClientExecutor {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailClientExecutor.class);

    /**
     * The delay between starting time stamp of each execution. 
     */
    private static final int executionDelay = 1;
    
    private AWSCloudTrailRecordReaderFactory readerFactory;
    private AWSCloudTrailClientConfiguration config;
    
    private SourceFilter sourceFilter;
    private RecordFilter recordFilter;
	private RecordsProcessor recordsEmitter;
	private ProgressReporter progressReporter;
	private ExceptionHandler exceptionHandler;
	
    /**
     * Scheduled thread pool used to periodically poll queue and enqueue jobs into our main thread pool executorService.
     * 
     * Note If any execution of this task takes longer than its period, then subsequent executions may start late, but 
     * will not concurrently execute.
     */
    private ScheduledExecutorService scheduledThreadPool;
    
    /**
     * The thread pool actually process log files.
     */
    private ExecutorService mainThreadPool;
    
    /**
     * Create an instance of AWSCloudTrailRecordExecutor. 
     * 
     * @param propertiesFile path to java properties file
     */
    public AWSCloudTrailClientExecutor(String propertiesFile) {
        this(propertiesFile, null);
    }
    
    /**
     * Create an instance of AWSCloudTrailRecordExecutor, passed in AWSCredentialsProvider will override
     * what specified in configuration property file.
     * 
     * Avoid use sessionCredentials if AWSCloudTrailClientExecutor is intended to be used as a long run process.
     * @param propertiesFile
     * @param credentialsProvider
     */
    public AWSCloudTrailClientExecutor(String propertiesFile, AWSCredentialsProvider credentialsProvider) {
    	this(propertiesFile, credentialsProvider, null);
    }
    
    
    /**
     * Create an instance of AWSCloudTrailRecordExecutor, passed in AWSCredentialsProvider and executorService will 
     * override what specified in configuration property file.
     * 
     * @param propertiesFile
     * @param credentialsProvider
     * @param executorService
     */
    public AWSCloudTrailClientExecutor(String propertiesFile, AWSCredentialsProvider credentialsProvider, ExecutorService executorService) {
    	this.config = new AWSCloudTrailClientConfiguration(propertiesFile);
    	
    	if (credentialsProvider != null) {
    		this.config.awsCredentialsProvider = credentialsProvider;
    	}

    	//crate a reader factory instance that will spawn AWSCloudTrailRecordReader object
    	final AWSCloudTrailExecutionFactory threadFactory = new AWSCloudTrailExecutionFactory(this.config);
    	this.scheduledThreadPool = threadFactory.createScheduledThreadPool();
    	
    	if (executorService == null) {
    		this.mainThreadPool = threadFactory.createMainThreadPool();
    	} else {
    		this.mainThreadPool = executorService;
    	}

    }

	/**
	 * Start to process AWS CloudTrail logs.
	 */
	public void start() {
		this.validate();
		
        this.readerFactory = new AWSCloudTrailRecordReaderFactory(this.recordsEmitter, this.sourceFilter, 
        		this.recordFilter, this.progressReporter, this.exceptionHandler, this.config);

		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					
					final AWSCloudTrailRecordReader reader = readerFactory.createReader();
					List<CloudTrailSource> sources = reader.getFilteredSources();
					
					for (final CloudTrailSource source : sources) {
	
						// process each AWSCloudTrailSource in main thread pool
						mainThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								reader.processSource(source);
							}
						});
					}
					
				} catch(Throwable t) {
					logger.error("Executor failed to process a task. " + t.getMessage(), t);
				}
			}
		}, 0L, executionDelay, TimeUnit.MICROSECONDS);
	}

    /**
     * Validate user's input before start processing logs.
     */
    private void validate() {
		if (this.config.awsCredentialsProvider == null) {
			throw new IllegalStateException("AWS Credential is null");
		}
		
		if (this.config.sqsUrl == null) {
			throw new IllegalStateException("SQS Url is null");
		}
		
		if (this.recordsEmitter == null) {
			throw new IllegalStateException("RecordsEmitter is null, please specify a RecordsEmitter.");
		}
	}

	/**
     * Stop to process AWS CloudTrail logs.
     * @throws InterruptedException 
     */
    public void stop() {
        stopThreadPool(this.mainThreadPool);
        stopThreadPool(this.scheduledThreadPool);
    }
    
    /**
     * Helper function to stop a ExecutorService
     * 
     * @param threadPool an instance of ExecutorService
     */
    private void stopThreadPool(ExecutorService threadPool) {
        if (threadPool != null) {
            
            if (threadPool.isShutdown()) {
                
                logger.info(threadPool.toString() + " is already stopped.");

            } else {

                logger.info(threadPool.toString() + " is about to shutdown.");
                
                // Shutdown thread pool
                threadPool.shutdown();

                // Wait for shutdown
                try {
                    threadPool.awaitTermination(this.config.threadTerminationDelay, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.info("Wait thread pool termination is interrupted.");
                }

                // ShutdownNow after waiting
                if (!threadPool.isShutdown()) {
                    threadPool.shutdownNow();
                }
                
                logger.info(threadPool + " is stopped.");
            }
        }
    }

	/**
	 * @param sourceFilter the sourceFilter to set
	 */
	public void setSourceFilter(SourceFilter sourceFilter) {
		this.sourceFilter = sourceFilter;
	}

	/**
	 * @param recordFilter the recordFilter to set
	 */
	public void setRecordFilter(RecordFilter recordFilter) {
		this.recordFilter = recordFilter;
	}
	
    /**
	 * @param recordsEmitter the emitter to set
	 */
	public void setRecordsEmitter(RecordsProcessor recordsEmitter) {
		this.recordsEmitter = recordsEmitter;
	}

	/**
	 * @param progressReporter the progressReporter to set
	 */
	public void setProgressReporter(ProgressReporter progressReporter) {
		this.progressReporter = progressReporter;
	}

	/**
	 * @param exceptionHandler the exceptionHandler to set
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
