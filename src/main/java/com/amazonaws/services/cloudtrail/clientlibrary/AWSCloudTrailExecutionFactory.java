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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class creates thread pool for AWSCloudTrailClientLibExecutor.
 */
public class AWSCloudTrailExecutionFactory {
	
	/**
	 * Main thread pool bounded queue size. 
	 */
	private static int blockingQueueSize = 1;
	
    private AWSCloudTrailClientConfiguration config;
    
    /**
     * A factory to create an instance of ExecutorService based on configuration
     * 
     * @param config
     */
    public AWSCloudTrailExecutionFactory(AWSCloudTrailClientConfiguration configuration) {
    	this.config = configuration;
    }
    
    /**
     * Create an instance of ScheduledExecutorService based on configuration
     * 
     * @return ScheduledExecutorService
     */
    public ScheduledExecutorService createScheduledThreadPool() {
        return Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Create an instance of ExecutorService. ExecutorService is AWSCloudTrailClient's main thread pool, 
     * used to process each sources. The thread pool queue, size are configurable through 
     * AWSCloudTrailClientConfiguration.
     * 
     * @return
     */
    public ExecutorService createMainThreadPool() {
    	if (this.config.threadCount < 1) {
    		throw new IllegalStateException("Thread Count cannot be less than 1.");
    	}
    	
    	return this.createScheduledExecutorWithBoundedQueue(this.config.threadCount);
    	
    }
    
    /**
     * Helper function to create an instance of ExecutorService bounded queue size.
     * 
     * When no more threads or queue slots are available because their bounds would be exceeded, the scheduled thread 
     * pool will run the rejected task directly. Unless the executor has been shut down, in which case the task
     * is discarded.
     * 
     * @return an instance of ExecutorService
     */
    private ExecutorService createScheduledExecutorWithBoundedQueue(int threadCount) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(blockingQueueSize);
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executorService = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS, 
        		blockingQueue, rejectedExecutionHandler);
        return executorService;        
    }
}
