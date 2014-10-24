/*******************************************************************************
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;

/**
 * A class used to obtain AWS CloudTrail Processing Library configuration
 * information from a classpath properties file.
 * <p>
 * In addition to this class, you can use {@link ClientConfiguration}
 * to manually set configuration options.
 */
public class PropertiesFileConfiguration implements ProcessingConfiguration{
    /* configuration file property names */
    public static final String ENABLE_RAW_RECORD_INFO = "enableRawRecordInfo";
    public static final String VERIFY_CLOUD_TRAIL_LOG_FILE = "verifyCloudTrailLogFile";
    public static final String MAX_RECORDS_PER_EMIT = "maxRecordsPerEmit";
    public static final String THREAD_COUNT = "threadCount";
    public static final String VISIBILITY_TIMEOUT = "visibilityTimeout";
    public static final String S3_REGION = "s3Region";
    public static final String SQS_REGION = "sqsRegion";
    public static final String SECRET_KEY = "secretKey";
    public static final String ACCESS_KEY = "accessKey";
    public static final String SQS_URL = "sqsUrl";

    private static final String ERROR_CREDENTIALS_PROVIDER_NULL = "The CredentialsProvider is null. Either set your " +
            "access key and secret key in a configuration file in your class path, or set it in the " +
            "configuration object you passed in.";

    /**
     * The URL of the SQS Queue to use to get CloudTrail logs.
     */
    private String sqsUrl = null;

    /**
     * AWS credentials provider
     */
    private AWSCredentialsProvider awsCredentialsProvider;

    /**
     * The SQS region.
     */
    private String sqsRegion = DEFAULT_SQS_REGION;

    /**
     * A period of time during which Amazon SQS prevents other consuming components
     * from receiving and processing that message.
     */
    private int visibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT;

    /**
     * The S3 end point specific to a region.
     */
    private String s3Region = DEFAULT_S3_REGION;

    /**
     * Number of threads to process log files in parallel.
     */
    private int threadCount = DEFAULT_THREAD_COUNT;

    /**
     * The duration in seconds to wait for thread pool termination before issue shutDownNow.
     */
    private int threadTerminationDelaySeconds = DEFAULT_THREAD_TERMINATION_DELAY_SECONDS;

    /**
     * Max number of AWSCloudTrailClientRecord that buffered before emit.
     */
    private int maxRecordsPerEmit = DEFAULT_MAX_RECORDS_PER_EMIT;

    /**
     * Whether to enable raw record in CloudTrailDeliveryInfo
     */
    private boolean enableRawRecordInfo = DEFAULT_ENABLE_RAW_RECORD_INFO;

    /**
     * Used by lower level API and high level API to create an instance of
     * {@link AWSCloudTrailRecordReaderConfiguration}
     *
     * @param propertiesFile the classpath properties file to load.
     */
    public PropertiesFileConfiguration(String propertiesFile) {
        //load properties from configuration properties file
        Properties prop = this.loadProperty(propertiesFile);

        this.sqsUrl = prop.getProperty(SQS_URL);
        LibraryUtils.checkArgumentNotNull(this.sqsUrl, "Cannot find sqsUrl in properties file.");

        String accessKey = prop.getProperty(ACCESS_KEY);
        String secretKey = prop.getProperty(SECRET_KEY);

        if (accessKey != null && secretKey != null) {
            this.awsCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider(propertiesFile);
        }

        this.s3Region = prop.getProperty(S3_REGION);
        this.visibilityTimeout = this.getIntProperty(prop, VISIBILITY_TIMEOUT);

        this.sqsRegion = prop.getProperty(SQS_REGION);

        this.threadCount = this.getIntProperty(prop, THREAD_COUNT);

        this.maxRecordsPerEmit = this.getIntProperty(prop, MAX_RECORDS_PER_EMIT);
        this.enableRawRecordInfo = this.getBooleanProperty(prop, ENABLE_RAW_RECORD_INFO);
    }

    /**
     * {@inheritDoc}
     */
    public String getSqsUrl() {
        return sqsUrl;
    }

    /**
     * {@inheritDoc}
     */
    public String getSqsRegion() {
        return sqsRegion;
    }

    /**
     * {@inheritDoc}
     */
    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * {@inheritDoc}
     */
    public String getS3Region() {
        return s3Region;
    }

    /**
     * {@inheritDoc}
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * {@inheritDoc}
     */
    public int getThreadTerminationDelaySeconds() {
        return threadTerminationDelaySeconds;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxRecordsPerEmit() {
        return maxRecordsPerEmit;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnableRawRecordInfo() {
        return enableRawRecordInfo;
    }

    /**
     * {@inheritDoc}
     */
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return awsCredentialsProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        LibraryUtils.checkArgumentNotNull(this.getAwsCredentialsProvider(), ERROR_CREDENTIALS_PROVIDER_NULL);
        LibraryUtils.checkArgumentNotNull(this.getSqsUrl(), "SQS URL is null.");
        LibraryUtils.checkArgumentNotNull(this.getMaxRecordsPerEmit(),  "Credential is null.");
        LibraryUtils.checkArgumentNotNull(this.getS3Region(),  "S3Region is null.");
        LibraryUtils.checkArgumentNotNull(this.getSqsRegion(),  "sqsRegion is null.");
        LibraryUtils.checkArgumentNotNull(this.getSqsUrl(),  "sqsUrl is null.");
        LibraryUtils.checkArgumentNotNull(this.getThreadCount(),  "threadCount is null.");
        LibraryUtils.checkArgumentNotNull(this.getThreadTerminationDelaySeconds(),  "threadTerminationDelaySeconds is null.");
        LibraryUtils.checkArgumentNotNull(this.getVisibilityTimeout(),  "visibilityTimeout is null.");
    }

    /**
     * Load properties from a classpath property file.
     *
     * @param propertiesFile the classpath properties file to use.
     * @return a {@Properties} object containing the properties set in the file.
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
     * Parse a string representation property to integer type
     *
     * @param prop property class
     * @param name name used in property file
     * @return
     */
    private int getIntProperty(Properties prop, String name) {
        String propertyValue = prop.getProperty(name);
        return Integer.parseInt(propertyValue);
    }

    /**
     * Parse a string representation property to integer type
     *
     * @param prop property class
     * @param name name used in property file
     * @return
     */
    private Boolean getBooleanProperty(Properties prop, String name) {
        String propertyValue = prop.getProperty(name);
        return Boolean.parseBoolean(propertyValue);
    }
}
