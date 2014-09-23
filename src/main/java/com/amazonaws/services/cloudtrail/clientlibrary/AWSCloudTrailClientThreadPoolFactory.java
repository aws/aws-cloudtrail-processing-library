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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;

/**
 * This class creates thread pool for AWSCloudTrailClientThreadPoolFactory.
 */
public class AWSCloudTrailClientThreadPoolFactory {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailClientThreadPoolFactory.class);

    /**
     * Number of current running thread to process CloudTrailSources
     */
    private int threadCount;

    /**
     * The exceptionHandler is used to handle uncaught exception.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * A factory to create an instance of ExecutorService based on configuration
     *
     * @param exceptionHandler
     * @param config
     */
    public AWSCloudTrailClientThreadPoolFactory(int threadCount, ExceptionHandler exceptionHandler) {
        this.threadCount = threadCount;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Create an instance of ScheduledExecutorService. We only need single thread to poll messages
     * from the SQS queue.
     *
     * @return ScheduledExecutorService continuous poll messages from SQS queue.
     */
    public ScheduledExecutorService createScheduledThreadPool() {
        return Executors.newScheduledThreadPool(1);
    }

    /**
     * Create an instance of ExecutorService. ExecutorService is AWSCloudTrailClient's main thread pool,
     * used to process each CloudTrailSource. The thread pool queue, size are configurable through
     * AWSCloudTrailClientConfiguration.
     *
     * @return ExecutorService that process CloudTrailSource
     */
    public ExecutorService createMainThreadPool() {
        ClientLibraryUtils.checkCondition(this.threadCount < 1, "Thread Count cannot be less than 1.");
        return this.createThreadPoolWithBoundedQueue(this.threadCount);

    }

    /**
     * Helper function to create an instance of ExecutorService with bounded queue size.
     *
     * When no more threads or queue slots are available because their bounds would be exceeded, the scheduled thread
     * pool will run the rejected task directly. Unless the executor has been shut down, in which case the task is
     * discarded. Note while scheduled thread poll is running rejected task, scheduled thread pool will not poll
     * more messages to process.
     *
     * @return an instance of ExecutorService
     */
    private ExecutorService createThreadPoolWithBoundedQueue(int threadCount) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(threadCount);
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executorService = new ClientLibraryThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                blockingQueue, rejectedExecutionHandler, this.exceptionHandler);
        return executorService;
    }

    /**
     * When unexpected behavior happened, for example runtimeException. ClientLibraryThreadPoolExecutor will handle
     * the exception by calling ExceptionHandler provided by end user.
     */
    public class ClientLibraryThreadPoolExecutor extends ThreadPoolExecutor {
        private ExceptionHandler exceptionHandler;
        public ClientLibraryThreadPoolExecutor(int corePoolSize,
                int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                BlockingQueue<Runnable> workQueue,
                RejectedExecutionHandler handler, ExceptionHandler exceptionHandler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            logger.debug("AWS CloudTrail Client Library created a thread " + t.getName());
        }

        @Override
        public void afterExecute(Runnable r, Throwable t) {
            try {
                if (t != null) {
                    logger.error("AWS CloudTrail Client Library encounted an uncaught exception. " + t.getMessage(), t);
                    ProgressStatus status = new ProgressStatus(ProgressState.uncaughtException, null);
                    this.exceptionHandler.handleException(new ClientLibraryException(t.getMessage(), status));
                }
            } finally {
                super.afterExecute(r, t);
                logger.debug("AWS CloudTrail Client Library completed execution of a runnable.");
            }
        }

    }
}
