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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;

/**
 * Defines a basic processing configuration for the AWS CloudTrail Processing Library.
 *
 * You can use instances of this class to configure an {@link AWSCloudTrailProcessingExecutor} as an alternative to using .
 */
public class ClientConfiguration implements ProcessingConfiguration{

    private static final String ERROR_CREDENTIALS_PROVIDER_NULL = "CredentialsProvider is null. Either put your " +
            "access key and secret key in configuration file in your class path, or set it in the " +
            "configuration object you passed in.";

    /**
     * The URL of the SQS Queue subscribed to CloudTrail to receive events.
     */
    public String sqsUrl = null;

    /**
     * The AWS credentials provider to use.
     */
    public AWSCredentialsProvider awsCredentialsProvider;

    /**
     * The SQS region to use.
     * <p>
     * If not specified, the default SQS region ({@value
     * ProcessingConfiguration#DEFAULT_SQS_REGION}) will be used.
     */
    public String sqsRegion = DEFAULT_SQS_REGION;

    /**
     * A period of time during which Amazon SQS prevents other consuming
     * components from receiving and processing that message.
     */
    public int visibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT;

    /**
     * The S3 endpoint specific to a region.
     * <p>
     * If not specified, the default S3 region will be used.
     */
    public String s3Region = DEFAULT_S3_REGION;

    /**
     * The number of threads used to process log files in parallel.
     */
    public int threadCount = DEFAULT_THREAD_COUNT;

    /**
     * The duration in seconds to wait for thread pool termination before
     * issuing shutDownNow.
     */
    public int threadTerminationDelaySeconds = DEFAULT_THREAD_TERMINATION_DELAY_SECONDS;

    /**
     * The maxiumum number of {@link AWSCloudTrailClientRecord} instances that
     * are buffered before emitting.
     */
    public int maxRecordsPerEmit = DEFAULT_MAX_RECORDS_PER_EMIT;

    /**
     * Whether to verify the CloudTrail log file's signature.
     * <p>
     * By default, cloud trail logs are verified.
     */
    public boolean verifyCloudTrailLogFile = DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE;

    /**
     * Whether to enable raw records in {@link CloudTrailDeliveryInfo}.
     */
    public boolean enableRawRecordInfo = DEFAULT_ENABLE_RAW_RECORD_INFO;

    /**
     * Initializes a new <code>ClientConfiguration</code>.
     * <p>
     * Both parameters are required.
     *
     * @see <a href="http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/ImportantIdentifiers.html">Queue and Message Identifiers</a>
     * @see <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/AWSCredentialsProvider.html">AWSCredentialsProvider</a>
     *
     * @param sqsUrl the SQS URL to use to get CloudTrail events.
     * @param awsCredentialsProvider The AWS Credentials provider to use to
     *   obtain AWS access credentials.
     */
    public ClientConfiguration(String sqsUrl, AWSCredentialsProvider awsCredentialsProvider) {
        this.sqsUrl = sqsUrl;
        this.awsCredentialsProvider = awsCredentialsProvider;
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
    @Override
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
     * Sets the SQS Region to use to get CloudTrail logs.
     * @param sqsRegion the sqsRegion to set
     */
    public void setSqsRegion(String sqsRegion) {
        this.sqsRegion = sqsRegion;
    }

    /**
     * Sets the SQS visibility timeout, during which SQS ignores other requests
     * for the
     * @param visibilityTimeout the visibilityTimeout to set
     */
    public void setVisibilityTimeout(int visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    /**
     * @param s3Region the s3Region to set
     */
    public void setS3Region(String s3Region) {
        this.s3Region = s3Region;
    }

    /**
     * @param threadCount the threadCount to set
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * @param threadTerminationDelaySeconds the threadTerminationDelaySeconds to set
     */
    public void setThreadTerminationDelaySeconds(int threadTerminationDelaySeconds) {
        this.threadTerminationDelaySeconds = threadTerminationDelaySeconds;
    }

    /**
     * @param maxRecordsPerEmit the maxRecordsPerEmit to set
     */
    public void setMaxRecordsPerEmit(int maxRecordsPerEmit) {
        this.maxRecordsPerEmit = maxRecordsPerEmit;
    }

    /**
     * @param verifyCloudTrailLogFile the verifyCloudTrailLogFile to set
     */
    public void setVerifyCloudTrailLogFile(boolean verifyCloudTrailLogFile) {
        this.verifyCloudTrailLogFile = verifyCloudTrailLogFile;
    }

    /**
     * Set whether or not raw record information should be returned in
     *
     * @param enableRawRecordInfo the enableRawRecordInfo to set
     */
    public void setEnableRawRecordInfo(boolean enableRawRecordInfo) {
        this.enableRawRecordInfo = enableRawRecordInfo;
    }

    /**
     * Set the AWS Credentials Provider used to obtain credentials to verify CloudTrail logs.
     *
     * @param awsCredentialsProvider the awsCredentialsProvider to set
     */
    public void setAwsCredentialsProvider(AWSCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

}
