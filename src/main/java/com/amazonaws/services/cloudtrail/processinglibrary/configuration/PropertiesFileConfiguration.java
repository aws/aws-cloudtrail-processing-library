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

package com.amazonaws.services.cloudtrail.processinglibrary.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * A class used to obtain AWS CloudTrail Processing Library configuration
 * information from a classpath properties file.
 * <p>
 * In addition to this class, you can use {@link ClientConfiguration} to manually set configuration options.
 * </p>
 */
public class PropertiesFileConfiguration implements ProcessingConfiguration{
    /* configuration file property names */
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretKey";
    public static final String SQS_URL = "sqsUrl";
    public static final String SQS_REGION = "sqsRegion";
    public static final String VISIBILITY_TIMEOUT = "visibilityTimeout";
    public static final String S3_REGION = "s3Region";
    public static final String THREAD_COUNT = "threadCount";
    public static final String NUM_OF_PARALLEL_READERS = "numOfParallelReaders";
    public static final String THREAD_TERMINATION_DELAY_SECONDS = "threadTerminationDelaySeconds";
    public static final String MAX_EVENTS_PER_EMIT = "maxEventsPerEmit";
    public static final String ENABLE_RAW_EVENT_INFO = "enableRawEventInfo";
    public static final String DELETE_MESSAGE_UPON_FAILURE = "deleteMessageUponFailure";

    private static final String ERROR_CREDENTIALS_PROVIDER_NULL = "CredentialsProvider is null. Either put your " +
            "access key and secret key in the configuration file in your class path, or spcify it in the " +
            "ProcessingConfiguration object.";

    /**
     * The
     * <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/AWSCredentialsProvider.html">AWS credentials provider</a>
     * used to obtain credentials.
     */
    private AWSCredentialsProvider awsCredentialsProvider;

    /**
     * The SQS Queue URL used to receive events.
     * <p>
     * The Queue must be subscribed to AWS CloudTrail.
     */
    private String sqsUrl = null;

    /**
     * The SQS region to use.
     * <p>
     * If not specified, the default SQS region ({@value ProcessingConfiguration#DEFAULT_SQS_REGION}) will be used.
     * </p>
     */
    private String sqsRegion = DEFAULT_SQS_REGION;

    /**
     * A period of time, in seconds, during which Amazon SQS prevents other consuming components from receiving and
     * processing messages that are currently being processed by the CloudTrail Processing Library on your behalf.
     */
    private int visibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT;

    /**
     * The S3 endpoint specific to a region.
     * <p>
     * If not specified, the default S3 region will be used.
     * </p>
     */
    private String s3Region = DEFAULT_S3_REGION;

    /**
     * The number of threads used to download log files from S3 in parallel.
     * <p>
     * Callbacks can be invoked from any thread.
     * </p>
     */
    private int threadCount = DEFAULT_THREAD_COUNT;


    /**
     * The number of threads used to get SQS messages
     */
    private int numOfParallelReaders = DEFAULT_NUM_OF_PARALLEL_READERS;

    /**
     * The time allowed, in seconds, for threads to shut down after {@link AWSCloudTrailProcessingExecutor#stop()} is
     * called.
     * <p>
     * Any threads still running beyond this time will be forcibly terminated.
     * </p>
     */
    private int threadTerminationDelaySeconds = DEFAULT_THREAD_TERMINATION_DELAY_SECONDS;

    /**
     * The maximum number of AWSCloudTrailClientEvents sent to a single invocation of processEvents().
     */
    private int maxEventsPerEmit = DEFAULT_MAX_EVENTS_PER_EMIT;

    /**
     * Whether to include raw event information in {@link CloudTrailEventMetadata}.
     */
    private boolean enableRawEventInfo = DEFAULT_ENABLE_RAW_EVENT_INFO;

    /**
     * Whether to delete messages if there is any failure during {@link SqsManager#parseMessage(List)} and
     * {@link EventReader#processSource(CloudTrailSource)}.
     */
    private boolean deleteMessageUponFailure = DEFAULT_DELETE_MESSAGE_UPON_FAILURE;
    /**
     * Creates a {@link PropertiesFileConfiguration} from values provided in a classpath properties file.
     *
     * @param propertiesFile the classpath properties file to load.
     */
    public PropertiesFileConfiguration(String propertiesFile) {
        //load properties from configuration properties file
        Properties prop = loadProperty(propertiesFile);

        sqsUrl = prop.getProperty(SQS_URL);
        LibraryUtils.checkArgumentNotNull(sqsUrl, "Cannot find SQS URL in properties file.");

        String accessKey = prop.getProperty(ACCESS_KEY);
        String secretKey = prop.getProperty(SECRET_KEY);

        if (accessKey != null && secretKey != null) {
            awsCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider(propertiesFile);
        }

        s3Region = prop.getProperty(S3_REGION);
        visibilityTimeout = getIntProperty(prop, VISIBILITY_TIMEOUT);

        sqsRegion = prop.getProperty(SQS_REGION);

        threadCount = getIntProperty(prop, THREAD_COUNT);
        numOfParallelReaders = getIntProperty(prop, NUM_OF_PARALLEL_READERS);
        threadTerminationDelaySeconds = getIntProperty(prop, THREAD_TERMINATION_DELAY_SECONDS);

        maxEventsPerEmit = getIntProperty(prop, MAX_EVENTS_PER_EMIT);
        enableRawEventInfo = getBooleanProperty(prop, ENABLE_RAW_EVENT_INFO);

        deleteMessageUponFailure  = getBooleanProperty(prop, DELETE_MESSAGE_UPON_FAILURE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return awsCredentialsProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqsUrl() {
        return sqsUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqsRegion() {
        return sqsRegion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getS3Region() {
        return s3Region;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumOfParallelReaders(){
        return numOfParallelReaders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getThreadTerminationDelaySeconds() {
        return threadTerminationDelaySeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxEventsPerEmit() {
        return maxEventsPerEmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnableRawEventInfo() {
        return enableRawEventInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeleteMessageUponFailure() {
        return deleteMessageUponFailure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        LibraryUtils.checkArgumentNotNull(getAwsCredentialsProvider(), ERROR_CREDENTIALS_PROVIDER_NULL);
        LibraryUtils.checkArgumentNotNull(getSqsUrl(), "SQS URL is null.");
        LibraryUtils.checkArgumentNotNull(getSqsRegion(), "SQS Region is null.");
        LibraryUtils.checkArgumentNotNull(getS3Region(), "S3 Region is null.");

        LibraryUtils.checkCondition(getMaxEventsPerEmit() <= 0, "Maximum Events Per Emit is a non-positive integer.");
        LibraryUtils.checkCondition(getThreadCount() <= 0, "Num of Parallel Readers Count is a non-positive integer.");
        LibraryUtils.checkCondition(getNumOfParallelReaders() <= 0, "Thread Count is a non-positive integer.");
        LibraryUtils.checkCondition(getThreadTerminationDelaySeconds() <= 0, "Thread Termination Delay Seconds is a non-positive integer.");
        LibraryUtils.checkCondition(getVisibilityTimeout() <= 0, "Visibility Timeout is a non-positive integer.");
    }

    /**
     * Load properties from a classpath property file.
     *
     * @param propertiesFile the classpath properties file to read.
     * @return a <a href="http://docs.oracle.com/javase/7/docs/api/java/util/Properties.html">Properties</a> object
     *     containing the properties set in the file.
     */
    private Properties loadProperty(String propertiesFile) {
        Properties prop = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream(propertiesFile);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load property file at " + propertiesFile, e);
        }
        return prop;
    }

    /**
     * Convert a string representation of a property to an integer type.
     *
     * @param prop the {@link Properties} needs conversion.
     * @param name a name to evaluate in the property file.
     * @return an integer representation of the value associated with the property name.
     */
    private int getIntProperty(Properties prop, String name) {
        String propertyValue = prop.getProperty(name);
        return Integer.parseInt(propertyValue);
    }

    /**
     * Convert a string representation of a property to a boolean type.
     *
     * @param prop the {@link Properties} needs conversion.
     * @param name a name to evaluate in the property file.
     * @return a boolean representation of the value associated with the property name.
     */
    private Boolean getBooleanProperty(Properties prop, String name) {
        String propertyValue = prop.getProperty(name);
        return Boolean.parseBoolean(propertyValue);
    }
}
