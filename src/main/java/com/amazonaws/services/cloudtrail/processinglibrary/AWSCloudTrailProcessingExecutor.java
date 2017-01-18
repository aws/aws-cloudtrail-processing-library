/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 ******************************************************************************/

package com.amazonaws.services.cloudtrail.processinglibrary;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.PropertiesFileConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.EventReaderFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.ThreadPoolFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultEventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultEventsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultSourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQSClient;

/**
 * AWS CloudTrail Processing Library's main execution logic. This class loads a user's configuration and
 * creates an {@link EventReaderFactory} object which spawns a {@link EventReader} to process log files.
 * <p>
 * It has two thread pools: <code>scheduledThreadPool</code>, which is a single-threaded scheduled
 * thread pool used to poll SQS for messages, and <code>mainThreadPool</code>, which has a
 * configurable size and processes each {@link CloudTrailSource} in parallel.
 */
public class AWSCloudTrailProcessingExecutor {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailProcessingExecutor.class);

    /**
     * The delay between starting time stamp of each execution, minimum 1 nanosecond.
     * In such a case it behaves as if it were continuously running.
     */
    private static final int EXECUTION_DELAY = 1; //1 nanosecond

    private static final String ERROR_CONFIGURATION_NULL = "ProcessingConfiguration object is null. " +
            "Either pass in a class path property file path or directly pass in a ProcessingConfiguration object";

    private ProcessingConfiguration config;
    private SourceFilter sourceFilter;
    private EventFilter eventFilter;
    private EventsProcessor eventsProcessor;
    private ProgressReporter progressReporter;
    private ExceptionHandler exceptionHandler;

    /**
     * Scheduled thread pool used to continuously poll queue and enqueue jobs
     * into our main thread pool executorService.
     */
    private ScheduledExecutorService scheduledThreadPool;

    /**
     * The thread pool that processes the log files.
     */
    private ExecutorService mainThreadPool;

    private EventReaderFactory readerFactory;

    private AWSCloudTrailProcessingExecutor(Builder builder) {
        this.config = builder.config;
        this.sourceFilter = builder.sourceFilter;
        this.eventFilter = builder.eventFilter;
        this.eventsProcessor = builder.eventsProcessor;
        this.progressReporter = builder.progressReporter;
        this.exceptionHandler = builder.exceptionHandler;

        this.scheduledThreadPool = builder.scheduledThreadPool;
        this.mainThreadPool = builder.mainThreadPool;
        this.readerFactory = builder.readerFactory;
    }

    /**
     * Start processing AWS CloudTrail logs.
     */
    public void start() {
        logger.info("Started AWSCloudTrailProcessingLibrary.");
        this.ValidateBeforeStart();
        scheduledThreadPool.scheduleAtFixedRate(new ScheduledJob(this.readerFactory), 0L, EXECUTION_DELAY, TimeUnit.MICROSECONDS);
    }

    /**
     * Stop processing AWS CloudTrail logs.
     */
    public void stop() {
        stopThreadPool(this.mainThreadPool);
        stopThreadPool(this.scheduledThreadPool);
        logger.info("Stopped AWSCloudTrailProcessingLibrary.");
    }

    /**
     * Helper function to gracefully stop an {@link ExecutorService}.
     *
     * @param threadPool the thread pool to stop.
     */
    private void stopThreadPool(ExecutorService threadPool) {
        LibraryUtils.checkCondition(threadPool == null, "Thread pool is null when calling stop");

        if (threadPool.isShutdown()) {
            logger.debug(threadPool.toString() + " is already stopped.");

        } else {

            logger.debug(threadPool.toString() + " is about to shutdown.");
            threadPool.shutdown(); // Shutdown thread pool

            try { // Wait for shutdown
                threadPool.awaitTermination(this.config.getThreadTerminationDelaySeconds(), TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.debug("Wait thread pool termination is interrupted.");
            }

            if (!threadPool.isShutdown()) { // ShutdownNow after waiting
                logger.debug(threadPool.toString() + " is force to shutdown now.");
                threadPool.shutdownNow();
            }

            logger.debug(threadPool.toString() + " is stopped.");
        }
    }

    /**
     * A job that runs in a scheduled thread pool.
     *
     * If any execution of the task raises an exception, then {@link ScheduledExecutorService} will
     * suppress any subsequent executions. Therefore, we try/catch a Throwable here.
     */
    private class ScheduledJob implements Runnable {
        private EventReaderFactory eventReaderFactory;

        public ScheduledJob (EventReaderFactory eventReaderFactory) {
            this.eventReaderFactory = eventReaderFactory;
        }

        /**
         * Run the scheduled job.
         */
        public void run() {
            try {
                final EventReader reader = eventReaderFactory.createReader();
                List<CloudTrailSource> sources = reader.getSources();
                for (final CloudTrailSource source : sources) {

                    // process each CloudTrailSource in main thread pool
                    mainThreadPool.execute(new Runnable() {
                        public void run() {
                            reader.processSource(source);
                        }
                    });
                }

            } catch (Throwable t) {
                logger.error("Executor failed to process a task. " + t.getMessage(), t);
            }
        }
    }

    /**
     * A class that builds an {@link AWSCloudTrailProcessingExecutor} object.
     */
    public static class Builder {
        private static final int SDK_TIME_OUT = 10000; // 10 seconds

        private ProcessingConfiguration config;

        //provide default implementation to AWSCloudTrailProcessingLibrary interfaces.
        private SourceFilter sourceFilter = new DefaultSourceFilter();
        private EventFilter eventFilter = new DefaultEventFilter();
        private EventsProcessor eventsProcessor = new DefaultEventsProcessor();
        private ProgressReporter progressReporter = new DefaultProgressReporter();
        private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

        private ScheduledExecutorService scheduledThreadPool;
        private ExecutorService mainThreadPool;
        private EventReaderFactory readerFactory;

        private String propertyFilePath;
        private AmazonS3Client s3Client;
        private AmazonSQSClient sqsClient;

        /**
         * Builder for {@link AWSCloudTrailProcessingExecutor}.
         *
         * @param eventsProcessor The {@link interfaces.EventsProcessor} that will process
         *     {@link model.CloudTrailEvent}s.
         * @param propertyFilePath The path to a property file containing the AWS CloudTrail Processing Library's
         *     configuration.
         */
        public Builder(EventsProcessor eventsProcessor, String propertyFilePath) {
            this.eventsProcessor = eventsProcessor;
            this.propertyFilePath= propertyFilePath;
        }

        /**
         * Builder for {@link AWSCloudTrailProcessingExecutor}.
         *
         * @param eventsProcessor The {@link interfaces.EventsProcessor} instance that will process
         *     {@link model.CloudTrailEvent}s.
         * @param config An {@link configuration.ProcessingConfiguration} instance that provides the library's
         *     configuration details.
         */
        public Builder(EventsProcessor eventsProcessor, ProcessingConfiguration config) {
            this.eventsProcessor = eventsProcessor;
            this.config = config;
        }

        /**
         * Applies a user-defined {@link interfaces.SourceFilter} to this instance.
         *
         * @param sourceFilter The <code>SourceFilter</code> that will be used to filter
         *                     {@link model.CloudTrailSource} source.
         * @return This <code>Builder</code> instance, using the specified <code>SourceFilter</code>.
         */
        public Builder withSourceFilter(SourceFilter sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        /**
         * Applies a user-defined {@link interfaces.EventFilter} to this instance.
         *
         * @param eventFilter The <code>EventFilter</code> that will be used to filter
         *                    {@link model.CloudTrailEvent}s.
         * @return This <code>Builder</code> instance, using the specified <code>EventFilter</code>.
         */
        public Builder withEventFilter(EventFilter eventFilter) {
            this.eventFilter = eventFilter;
            return this;
        }

        /**
         * Applies a user-defined {@link interfaces.ProgressReporter} to this instance.
         *
         * @param progressReporter The <code>ProgressReporter</code> that will report
         *                         the state of the AWSCloudTrailProcessingLibrary processing process
         *
         * @return This <code>Builder</code> instance, using the specified <code>ProgressReporter</code>.
         */
        public Builder withProgressReporter(ProgressReporter progressReporter) {
            this.progressReporter = progressReporter;
            return this;
        }

        /**
         * Applies a user-defined {@link interfaces.ExceptionHandler} to this instance.
         *
         * @param exceptionHandler The <code>ExceptionHandler</code> that will handle exceptions for
         *                         this instance.
         *
         * @return This <code>Builder</code> instance, using the specified
         *         <code>ExceptionHandler</code>.
         */
        public Builder withExceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Applies a user-defined <a
         * href="http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html">ExecutorService</a>
         * thread pool to this instance.
         *
         * @param mainThreadPool The <code>ExecutorService</code> thread pool that will be used to
         *                       process CloudTrailSource
         *
         * @return This <code>Builder</code> instance, using the specified thread pool.
         */
        public Builder withThreadPool(ExecutorService mainThreadPool) {
            this.mainThreadPool = mainThreadPool;
            return this;
        }

        /**
         * Applies a user-defined <a
         * href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonS3Client.html">AmazonS3Client</a>
         * to this instance.
         *
         * @param s3Client the <code>AmazonS3Client</code> object used to download CloudTrail log files
         *
         * @return This <code>Builder</code> instance, using the specified
         *         <code>AmazonS3Client</code>.
         */
        public Builder withS3Client(AmazonS3Client s3Client) {
            this.s3Client = s3Client;
            return this;
        }

        /**
         * Applies a user-defined <a
         * href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQSClient.html">AmazonSQSClient</a>
         * to this instance.
         *
         * @param sqsClient The <code>AmazonSQSClient</code> that will be used to poll messages from
         *                  the SQS queue.
         *
         * @return This <code>Builder</code> instance, using the specified
         *         <code>AmazonSQSClient</code>.
         */
        public Builder withSQSClient(AmazonSQSClient sqsClient) {
            this.sqsClient = sqsClient;
            return this;
        }

        /**
         * Build an {@link AWSCloudTrailProcessingExecutor} using the classpath property file.
         *
         * @return an AWSCloudTrailProcessingExecutor instance.
         */
        public AWSCloudTrailProcessingExecutor build() {
            // passed in configuration as property file
            if (this.config == null && this.propertyFilePath != null) {
                this.config = new PropertiesFileConfiguration(propertyFilePath);
            }

            LibraryUtils.checkArgumentNotNull(this.config, ERROR_CONFIGURATION_NULL);
            LibraryUtils.checkArgumentNotNull(this.config.getAwsCredentialsProvider(),
                    "ProcessingConfiguration missing AWSCredentialsProvider attribute");

            LibraryUtils.checkArgumentNotNull(this.eventsProcessor, "eventsProcessor is null.");
            LibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null.");
            LibraryUtils.checkArgumentNotNull(this.eventFilter, "eventFilter is null.");
            LibraryUtils.checkArgumentNotNull(this.progressReporter, "progressReporter is null.");
            LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exceptionHandler is null.");

            // override default timeout for S3Client
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setConnectionTimeout(SDK_TIME_OUT);
            clientConfiguration.setSocketTimeout(SDK_TIME_OUT);

            if (this.s3Client == null) {
                AmazonS3Client s3Client = new AmazonS3Client(this.config.getAwsCredentialsProvider(), clientConfiguration);
                s3Client.setRegion(Region.getRegion(Regions.fromName(this.config.getS3Region())));
                this.s3Client = s3Client;
            }

            if (this.sqsClient == null) {
                AmazonSQSClient sqsClient = new AmazonSQSClient(config.getAwsCredentialsProvider());
                sqsClient.setRegion(Region.getRegion(Regions.fromName(this.config.getSqsRegion())));
                this.sqsClient = sqsClient;
            }

            LibraryUtils.checkArgumentNotNull(this.s3Client, "s3Client is null.");
            LibraryUtils.checkArgumentNotNull(this.sqsClient, "sqsClient is null.");

            SqsManager sqsManager = new SqsManager(sqsClient, this.config, this.exceptionHandler, this.progressReporter);
            S3Manager s3Manager= new S3Manager(s3Client, this.config, this.exceptionHandler, this.progressReporter);

            this.readerFactory = new EventReaderFactory.Builder(this.config)
                .withEventsProcessor(this.eventsProcessor)
                .withSourceFilter(this.sourceFilter)
                .withEventFilter(this.eventFilter)
                .withProgressReporter(this.progressReporter)
                .withExceptionHandler(this.exceptionHandler)
                .withS3Manager(s3Manager)
                .withSQSManager(sqsManager).build();

            LibraryUtils.checkArgumentNotNull(this.readerFactory, "readerFactory is null.");

            ThreadPoolFactory threadFactory = new ThreadPoolFactory(this.config.getThreadCount(), this.exceptionHandler);
            this.scheduledThreadPool = threadFactory.createScheduledThreadPool();

            if (this.mainThreadPool == null) {
                this.mainThreadPool = threadFactory.createMainThreadPool();
            }

            LibraryUtils.checkArgumentNotNull(this.scheduledThreadPool, "scheduledThreadPoll is null.");
            LibraryUtils.checkArgumentNotNull(this.mainThreadPool, "mainThreadPool is null.");

            return new AWSCloudTrailProcessingExecutor(this);
        }
    }

    /**
     * Validate the user's input before processing logs.
     */
    private void ValidateBeforeStart() {
        LibraryUtils.checkArgumentNotNull(this.config, "Configuration is null.");
        this.config.validate();

        LibraryUtils.checkArgumentNotNull(this.eventsProcessor, "eventsProcessor is null.");
        LibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null.");
        LibraryUtils.checkArgumentNotNull(this.eventFilter, "eventFilter is null.");
        LibraryUtils.checkArgumentNotNull(this.progressReporter, "progressReporter is null.");
        LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exceptionHandler is null.");

        LibraryUtils.checkArgumentNotNull(this.scheduledThreadPool, "scheduledThreadPool is null.");
        LibraryUtils.checkArgumentNotNull(this.mainThreadPool, "mainThreadPool is null.");
        LibraryUtils.checkArgumentNotNull(this.readerFactory, "readerFactory is null.");
    }
}
