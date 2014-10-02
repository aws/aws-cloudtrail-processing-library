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
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ClasspathPropertiesFileProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.RecordReaderFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.ThreadPoolFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultRecordFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultRecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultSourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.RecordReader;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQSClient;

/**
 * AWSCloudTrail Processing Library's main execution logic. It loads a user's configuration and creates an
 * RecordReaderFactory object which spawns a RecordReader to process log files. It has
 * two thread pools. The first is scheduledThreadPool which is a single-threaded scheduled thread pool
 * used to poll SQS for messages. The second thread pool has a configurable size and processes each
 * CloudTrailSource in parallel.
 */
public class AWSCloudTrailProcessingExecutor {
    private static final Log logger = LogFactory.getLog(AWSCloudTrailProcessingExecutor.class);

    /**
     * The delay between starting time stamp of each execution, minimum 1 nanosecond.
     * In such a case it behaves as if it were continuously running.
     */
    private static final int EXECUTION_DELAY = 1; //1 nanosecond

    private static final String ERROR_CONFIGURATION_NULL = "ProcessingConfiguraiton object is null. " +
            "Either pass in a class path property file path or directly pass in a ProcessingConfiguraiton object";

    private ProcessingConfiguration config;
    private SourceFilter sourceFilter;
    private RecordFilter recordFilter;
    private RecordsProcessor recordsProcessor;
    private ProgressReporter progressReporter;
    private ExceptionHandler exceptionHandler;

    /**
     * Scheduled thread pool used to continuously poll queue and enqueue jobs
     * into our main thread pool executorService.
     */
    private ScheduledExecutorService scheduledThreadPool;

    /**
     * The thread pool which processes the log files.
     *
     */
    private ExecutorService mainThreadPool;

    private RecordReaderFactory readerFactory;

    private AWSCloudTrailProcessingExecutor(Builder builder) {
        this.config = builder.config;
        this.sourceFilter = builder.sourceFilter;
        this.recordFilter = builder.recordFilter;
        this.recordsProcessor = builder.recordsProcessor;
        this.progressReporter = builder.progressReporter;
        this.exceptionHandler = builder.exceptionHandler;

        this.scheduledThreadPool = builder.scheduledThreadPool;
        this.mainThreadPool = builder.mainThreadPool;
        this.readerFactory = builder.readerFactory;
    }

    /**
     * Start to process AWS CloudTrail logs.
     */
    public void start() {
        logger.info("Started AWSCloudTrailClientLibrary.");
        this.ValidateBeforeStart();
        scheduledThreadPool.scheduleAtFixedRate(new ScheduledJob(this.readerFactory), 0L, EXECUTION_DELAY, TimeUnit.MICROSECONDS);
    }

    /**
     * Stop processing AWS CloudTrail logs.
     *
     * @throws InterruptedException
     */
    public void stop() {
        stopThreadPool(this.mainThreadPool);
        stopThreadPool(this.scheduledThreadPool);
        logger.info("Stopped AWSCloudTrailClientLibrary.");
    }

    /**
     * Helper function to gracefully stop an ExecutorService.
     *
     * @param threadPool the thread pool we need to stop.
     */
    private void stopThreadPool(ExecutorService threadPool) {
        LibraryUtils.checkCondition(threadPool == null, "Thread pool is null when calling stop");

        if (threadPool.isShutdown()) {
            logger.debug(threadPool.toString() + " is already stopped.");

        } else {

            logger.debug(threadPool.toString() + " is about to shutdown.");
            threadPool.shutdown(); // Shutdown thread pool

            try { // Wait for shutdown
                threadPool.awaitTermination(this.config.getThreadTerminationDelay(), TimeUnit.SECONDS);
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
     * The job that runs in scheduled thread pool.
     *
     * If any execution of the task encounters an exception, ScheduledExecutorService will suppress subsequent executions.
     * Therefore we try catch Throwable at here.
     */
    private class ScheduledJob implements Runnable {
        private RecordReaderFactory recordReaderFactory;

        public ScheduledJob (RecordReaderFactory recordReaderFactory) {
            this.recordReaderFactory = recordReaderFactory;
        }

        public void run() {
            try {
                final RecordReader reader = recordReaderFactory.createReader();
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
     * To build an AWSCloudTrailProcessingExecutor object.
     * @author simonguo
     *
     */
    public static class Builder {
        private static final int SDK_TIME_OUT = 10000; //10 seconds

        private ProcessingConfiguration config;

        //provide default implementation to AWSCloudTrailClientLibrary interfaces.
        private SourceFilter sourceFilter = new DefaultSourceFilter();
        private RecordFilter recordFilter = new DefaultRecordFilter();
        private RecordsProcessor recordsProcessor = new DefaultRecordsProcessor();
        private ProgressReporter progressReporter = new DefaultProgressReporter();
        private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

        private ScheduledExecutorService scheduledThreadPool;
        private ExecutorService mainThreadPool;
        private RecordReaderFactory readerFactory;

        private String propertyFilePath;
        private AmazonS3Client s3Client;
        private AmazonSQSClient sqsClient;

        /**
         * Builder for AWSCloudTrailClientExecutor
         *
         * @param recordsProcessor that process CloudTrailClientRecords
         * @param propertyFilePath the path to property file containing AWSCloudTrailClientLibrary configuration.
         */
        public Builder(RecordsProcessor recordsProcessor, String propertyFilePath) {
            this.recordsProcessor = recordsProcessor;
            this.propertyFilePath= propertyFilePath;
        }

        /**
         * Builder for AWSCloudTrailProcessingExecutor
         *
         * @param recordsProcessor that process CloudTrailClientRecords
         * @param config the AWSCloudTrailClientLibrary configuration.
         */
        public Builder(RecordsProcessor recordsProcessor, ProcessingConfiguration config) {
            this.recordsProcessor = recordsProcessor;
            this.config = config;
        }

        /**
         * Specify user defined SourceFilter
         *
         * @param sourceFilter to filter CloudTrailSource
         * @return
         */
        public Builder withSourceFilter(SourceFilter sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        /**
         * Specify user defined RecordFilter
         *
         * @param recordFilter to filter CloudTrailClientRecord
         * @return
         */
        public Builder withRecordFilter(RecordFilter recordFilter) {
            this.recordFilter = recordFilter;
            return this;
        }

        /**
         * Specify user defined ProgressReporter
         *
         * @param progressReporter report AWSCloudTrailClientLibrary processing process
         * @return
         */
        public Builder withProgressReporter(ProgressReporter progressReporter) {
            this.progressReporter = progressReporter;
            return this;
        }

        /**
         * Specify user defined ExceptionHandler
         *
         * @param exceptionHandler handle exception cases
         * @return
         */
        public Builder withExceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Specify user defined thread pool - ExecutorService
         * @param mainThreadPool the thread pool to process CloudTrailSource
         * @return
         */
        public Builder withThreadPool(ExecutorService mainThreadPool) {
            this.mainThreadPool = mainThreadPool;
            return this;
        }

        /**
         * Specify user defined S3Client
         *
         * @param s3Client to download CloudTrail log files
         * @return
         */
        public Builder withS3Client(AmazonS3Client s3Client) {
            this.s3Client = s3Client;
            return this;
        }

        /**
         * Specify user defined SQSClient
         *
         * @param sqsClient to poll messages from SQS queue.
         * @return
         */
        public Builder withSQSClient(AmazonSQSClient sqsClient) {
            this.sqsClient = sqsClient;
            return this;
        }

        public AWSCloudTrailProcessingExecutor build() {
            // passed in configuration as property file
            if (this.config == null && this.propertyFilePath != null) {
                this.config = new ClasspathPropertiesFileProcessingConfiguration(propertyFilePath);
            }

            LibraryUtils.checkArgumentNotNull(this.config, ERROR_CONFIGURATION_NULL);
            LibraryUtils.checkArgumentNotNull(this.config.getAwsCredentialsProvider(), "AWSCloudTrailClientConfiguration miss AWSCredentialsProvider attribute");

            LibraryUtils.checkArgumentNotNull(this.recordsProcessor, "recordsProcessor is null.");
            LibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null.");
            LibraryUtils.checkArgumentNotNull(this.recordFilter, "recordFilter is null.");
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

            this.readerFactory = new RecordReaderFactory.Builder(this.config)
                .withRecordsProcessor(this.recordsProcessor)
                .withSourceFilter(this.sourceFilter)
                .withRecordFilter(this.recordFilter)
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
     * Validate user's input before start processing logs.
     */
    private void ValidateBeforeStart() {
        LibraryUtils.checkArgumentNotNull(this.config, "Configuration is null.");
        this.config.validate();

        LibraryUtils.checkArgumentNotNull(this.recordsProcessor, "RecordsProcessor is null.");
        LibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null.");
        LibraryUtils.checkArgumentNotNull(this.recordFilter, "recordFilter is null.");
        LibraryUtils.checkArgumentNotNull(this.progressReporter, "progressReporter is null.");
        LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exceptionHandler is null.");

        LibraryUtils.checkArgumentNotNull(this.scheduledThreadPool, "scheduledThreadPool is null.");
        LibraryUtils.checkArgumentNotNull(this.mainThreadPool, "mainThreadPool is null.");
        LibraryUtils.checkArgumentNotNull(this.readerFactory, "readerFactory is null.");
    }
}
