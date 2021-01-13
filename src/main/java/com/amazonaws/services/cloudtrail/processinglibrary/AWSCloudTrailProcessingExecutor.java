/*******************************************************************************
 * Copyright 2010-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.PropertiesFileConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.EventReaderFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.SourceSerializerFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.factory.ThreadPoolFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultEventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultEventsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.impl.DefaultSourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.SourceSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
        validateBeforeStart();
        scheduledThreadPool.scheduleAtFixedRate(new ScheduledJob(readerFactory), 0L, EXECUTION_DELAY, TimeUnit.MICROSECONDS);
    }

    /**
     * Stop processing AWS CloudTrail logs.
     */
    public void stop() {
        stopThreadPool(mainThreadPool);
        stopThreadPool(scheduledThreadPool);
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
                threadPool.awaitTermination(config.getThreadTerminationDelaySeconds(), TimeUnit.SECONDS);
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
     * <p>
     * If any execution of the task raises an exception, then {@link ScheduledExecutorService} will
     * suppress any subsequent executions. Therefore, we try/catch a Throwable here.
     */
    private class ScheduledJob implements Runnable {
        private EventReaderFactory eventReaderFactory;

        public ScheduledJob(EventReaderFactory eventReaderFactory) {
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
     * Validate the user's input before processing logs.
     */
    private void validateBeforeStart() {
        LibraryUtils.checkArgumentNotNull(config, "Configuration is null.");
        config.validate();

        LibraryUtils.checkArgumentNotNull(sourceFilter, "sourceFilter is null.");
        LibraryUtils.checkArgumentNotNull(eventFilter, "eventFilter is null.");
        LibraryUtils.checkArgumentNotNull(eventsProcessor, "eventsProcessor is null.");
        LibraryUtils.checkArgumentNotNull(progressReporter, "progressReporter is null.");
        LibraryUtils.checkArgumentNotNull(exceptionHandler, "exceptionHandler is null.");

        LibraryUtils.checkArgumentNotNull(scheduledThreadPool, "scheduledThreadPool is null.");
        LibraryUtils.checkArgumentNotNull(mainThreadPool, "mainThreadPool is null.");
        LibraryUtils.checkArgumentNotNull(readerFactory, "readerFactory is null.");
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

        private SourceSerializer sourceSerializer = SourceSerializerFactory.createSourceSerializerChain();
        private String propertyFilePath;
        private AmazonS3 s3Client;
        private AmazonSQS sqsClient;

        /**
         * Builder for {@link AWSCloudTrailProcessingExecutor}.
         *
         * @param eventsProcessor  The {@link EventsProcessor} that will process {@link CloudTrailEvent}s.
         * @param propertyFilePath The path to a property file containing the AWS CloudTrail Processing Library's
         *                         configuration.
         */
        public Builder(EventsProcessor eventsProcessor, String propertyFilePath) {
            this.eventsProcessor = eventsProcessor;
            this.propertyFilePath = propertyFilePath;
        }

        /**
         * Builder for {@link AWSCloudTrailProcessingExecutor}.
         *
         * @param eventsProcessor The {@link EventsProcessor} instance that will process {@link CloudTrailEvent}s.
         * @param config          An {@link ProcessingConfiguration} instance that provides the library's
         *                        configuration details.
         */
        public Builder(EventsProcessor eventsProcessor, ProcessingConfiguration config) {
            this.eventsProcessor = eventsProcessor;
            this.config = config;
        }

        /**
         * Applies a user-defined {@link SourceFilter} to this instance.
         *
         * @param sourceFilter The <code>SourceFilter</code> that will be used to filter {@link CloudTrailSource} source.
         * @return This <code>Builder</code> instance, using the specified <code>SourceFilter</code>.
         */
        public Builder withSourceFilter(SourceFilter sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        /**
         * Applies a user-defined {@link EventFilter} to this instance.
         *
         * @param eventFilter The <code>EventFilter</code> that will be used to filter {@link CloudTrailEvent}s.
         * @return This <code>Builder</code> instance, using the specified <code>EventFilter</code>.
         */
        public Builder withEventFilter(EventFilter eventFilter) {
            this.eventFilter = eventFilter;
            return this;
        }

        /**
         * Applies a user-defined {@link ProgressReporter} to this instance.
         *
         * @param progressReporter The <code>ProgressReporter</code> that will report
         *                         the state of the AWSCloudTrailProcessingLibrary processing process.
         * @return This <code>Builder</code> instance, using the specified <code>ProgressReporter</code>.
         */
        public Builder withProgressReporter(ProgressReporter progressReporter) {
            this.progressReporter = progressReporter;
            return this;
        }

        /**
         * Applies a user-defined {@link ExceptionHandler} to this instance.
         *
         * @param exceptionHandler The <code>ExceptionHandler</code> that will handle exceptions for
         *                         this instance.
         * @return This <code>Builder</code> instance, using the specified
         * <code>ExceptionHandler</code>.
         */
        public Builder withExceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        /**
         * Applies a user-defined {@link SourceSerializer} to this instance.
         *
         * @param sourceSerializer The<code>SourceSerializer</code> that gets the {@link CloudTrailSource} from the SQS message
         *                         object for this instance.
         * @return This <code>Builder</code> instance, using the specified <code>SourceSerializer</code>
         */
        public Builder withSourceSerializer(SourceSerializer sourceSerializer) {
            this.sourceSerializer = sourceSerializer;
            return this;
        }

        /**
         * Applies a user-defined <a
         * href="http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html">ExecutorService</a>
         * thread pool to this instance.
         *
         * @param mainThreadPool The <code>ExecutorService</code> thread pool that will be used to
         *                       process CloudTrailSource
         * @return This <code>Builder</code> instance, using the specified thread pool.
         */
        public Builder withThreadPool(ExecutorService mainThreadPool) {
            this.mainThreadPool = mainThreadPool;
            return this;
        }

        /**
         * Applies a user-defined <a
         * href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3.html">AmazonS3</a>
         * to this instance.
         *
         * @param s3Client the <code>AmazonS3</code> object used to download CloudTrail log files
         * @return This <code>Builder</code> instance, using the specified <code>AmazonS3</code>.
         */
        public Builder withS3Client(AmazonS3 s3Client) {
            this.s3Client = s3Client;
            return this;
        }

        /**
         * Applies a user-defined <a
         * href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQS.html">AmazonSQS</a>
         * to this instance.
         *
         * @param sqsClient The <code>AmazonSQS</code> that will be used to poll messages from
         *                  the SQS queue.
         * @return This <code>Builder</code> instance, using the specified
         *         <code>AmazonSQS</code>.
         */
        public Builder withSQSClient(AmazonSQS sqsClient) {
            this.sqsClient = sqsClient;
            return this;
        }

        /**
         * Build an {@link AWSCloudTrailProcessingExecutor} using the classpath property file.
         *
         * @return an AWSCloudTrailProcessingExecutor instance.
         */
        public AWSCloudTrailProcessingExecutor build() {
            buildConfig();
            validateBeforeBuild();
            buildS3Client();
            buildSqsClient();
            buildReaderFactory();
            buildThreadPools();

            return new AWSCloudTrailProcessingExecutor(this);
        }

        private void buildConfig() {
            // passed in configuration as property file
            if (config == null && propertyFilePath != null) {
                config = new PropertiesFileConfiguration(propertyFilePath);
            }
        }

        private void validateBeforeBuild() {
            LibraryUtils.checkArgumentNotNull(config, ERROR_CONFIGURATION_NULL);
            LibraryUtils.checkArgumentNotNull(config.getAwsCredentialsProvider(),
                    "ProcessingConfiguration missing AWSCredentialsProvider attribute");

            LibraryUtils.checkArgumentNotNull(eventsProcessor, "eventsProcessor is null.");
            LibraryUtils.checkArgumentNotNull(sourceFilter, "sourceFilter is null.");
            LibraryUtils.checkArgumentNotNull(eventFilter, "eventFilter is null.");
            LibraryUtils.checkArgumentNotNull(progressReporter, "progressReporter is null.");
            LibraryUtils.checkArgumentNotNull(exceptionHandler, "exceptionHandler is null.");
            LibraryUtils.checkArgumentNotNull(sourceSerializer, "sourceSerializer is null.");
        }

        private void buildS3Client() {
            // override default timeout for S3Client
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setConnectionTimeout(SDK_TIME_OUT);
            clientConfiguration.setSocketTimeout(SDK_TIME_OUT);
            clientConfiguration.setMaxConnections(Math.max(clientConfiguration.DEFAULT_MAX_CONNECTIONS, config.getThreadCount()));

            if (s3Client == null) {
                s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(config.getAwsCredentialsProvider())
                        .withClientConfiguration(clientConfiguration)
                        .withRegion(config.getS3Region())
                        .build();
            }
        }
        private void buildSqsClient() {
            if (sqsClient == null) {
                ClientConfiguration clientConfiguration = new ClientConfiguration();
                clientConfiguration.setMaxConnections(Math.max(clientConfiguration.DEFAULT_MAX_CONNECTIONS, config.getThreadCount()));
                sqsClient = AmazonSQSClientBuilder.standard()
                        .withCredentials(config.getAwsCredentialsProvider())
                        .withClientConfiguration(clientConfiguration)
                        .withRegion(config.getSqsRegion())
                        .build();
            }
        }

        private void buildReaderFactory() {
            SqsManager sqsManager = new SqsManager(sqsClient, config, exceptionHandler, progressReporter, sourceSerializer);
            S3Manager s3Manager = new S3Manager(s3Client, config, exceptionHandler, progressReporter);

            readerFactory = new EventReaderFactory.Builder(config)
                    .withEventsProcessor(eventsProcessor)
                    .withSourceFilter(sourceFilter)
                    .withEventFilter(eventFilter)
                    .withProgressReporter(progressReporter)
                    .withExceptionHandler(exceptionHandler)
                    .withS3Manager(s3Manager)
                    .withSQSManager(sqsManager).build();
        }

        private void buildThreadPools() {
            ThreadPoolFactory threadFactory = new ThreadPoolFactory(config.getThreadCount(), exceptionHandler);
            scheduledThreadPool = threadFactory.createScheduledThreadPool(config.getNumOfParallelReaders());

            if (mainThreadPool == null) {
                mainThreadPool = threadFactory.createMainThreadPool();
            }
        }
    }
}
