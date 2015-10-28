/*******************************************************************************
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * You can use instances of this class to configure an
 * {@link com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor}
 * as an alternative to using a class path properties file.
 */
public class ClientConfiguration implements ProcessingConfiguration{

    private static final String ERROR_CREDENTIALS_PROVIDER_NULL = "CredentialsProvider is null. Either put your " +
            "access key and secret key in the configuration file in your class path, or spcify it in the " +
            "ProcessingConfiguration object.";

    /**
     * The <a
     * href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/AWSCredentialsProvider.html">AWS credentials provider</a>
     * used to obtain credentials.
     */
    public AWSCredentialsProvider awsCredentialsProvider;

    /**
     * The SQS Queue URL used to receive events.
     * <p>
     * The Queue must be subscribed to AWS CloudTrail.
     */
    public String sqsUrl = null;

    /**
     * The SQS region to use.
     * <p>
     * If not specified, the default SQS region
     * ({@value com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration#DEFAULT_SQS_REGION})
     * will be used.
     */
    public String sqsRegion = DEFAULT_SQS_REGION;

    /**
     * A period of time, in seconds, during which Amazon SQS prevents other consuming components from receiving and
     * processing messages that are currently being processed by the CloudTrail Processing Library on your behalf.
     */
    public int visibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT;

    /**
     * The S3 endpoint specific to a region.
     * <p>
     * If not specified, the default S3 region will be used.
     */
    public String s3Region = DEFAULT_S3_REGION;

    /**
     * The number of threads used to download log files from S3 in parallel.
     * <p>
     * Callbacks can be invoked from any thread.
     */
    public int threadCount = DEFAULT_THREAD_COUNT;

    /**
     * The time allowed, in seconds, for threads to shut down after AWSCloudTrailEventProcessingExecutor.stop() is
     * called.
     * <p>
     * Any threads still running beyond this time will be forcibly terminated.
     */
    public int threadTerminationDelaySeconds = DEFAULT_THREAD_TERMINATION_DELAY_SECONDS;

    /**
     * The maximum number of AWSCloudTrailClientEvents sent to a single invocation of processEvents().
     */
    public int maxEventsPerEmit = DEFAULT_MAX_EVENTS_PER_EMIT;

    /**
     * Whether to include raw event information in
     * {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata}.
     */
    public boolean enableRawEventInfo = DEFAULT_ENABLE_RAW_EVENT_INFO;

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
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return awsCredentialsProvider;
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
    public void validate() {
        LibraryUtils.checkArgumentNotNull(this.getAwsCredentialsProvider(), ERROR_CREDENTIALS_PROVIDER_NULL);
        LibraryUtils.checkArgumentNotNull(this.getSqsUrl(), "SQS URL is null.");
        LibraryUtils.checkArgumentNotNull(this.getSqsRegion(), "SQS Region is null.");
        LibraryUtils.checkArgumentNotNull(this.getVisibilityTimeout(), "Visibility Timeout is null.");
        LibraryUtils.checkArgumentNotNull(this.getS3Region(), "S3 Region is null.");
        LibraryUtils.checkArgumentNotNull(this.getThreadCount(), "Thread Count is null.");
        LibraryUtils.checkArgumentNotNull(this.getThreadTerminationDelaySeconds(), "Thread Termination Delay Seconds is null.");
        LibraryUtils.checkArgumentNotNull(this.getMaxEventsPerEmit(), "Maximum Events Per Emit is null.");
        LibraryUtils.checkArgumentNotNull(this.isEnableRawEventInfo(), "Is Enable Raw Event Information is null.");
    }

    /**
     * Set the AWS Credentials Provider used to access AWS.
     *
     * @param awsCredentialsProvider the
     *     <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/AWSCredentialsProvider.html">AWSCredentialsProvider</a>
     *     to set.
     */
    public void setAwsCredentialsProvider(AWSCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    /**
     * Sets the SQS Region to use to get CloudTrail logs.
     *
     * @param sqsRegion the
     *     <a href="http://docs.aws.amazon.com/general/latest/gr/rande.html#d0e387">AWS region</a>
     *     to use.
     */
    public void setSqsRegion(String sqsRegion) {
        this.sqsRegion = sqsRegion;
    }

    /**
     * Sets the SQS visibility timeout, during which SQS ignores other requests
     * for the message.
     *
     * @param visibilityTimeout the duration, in seconds, to ignore other
     *     requests for SQS messages being processed by the AWS CloudTrail
     *     Processing Library.
     */
    public void setVisibilityTimeout(int visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    /**
     * The S3 endpoint specific to a region.
     * <p>
     * If not specified, the default S3 region will be used.
     *
     * @param s3Region the s3Region to set
     */
    public void setS3Region(String s3Region) {
        this.s3Region = s3Region;
    }

    /**
     * The number of threads used to download log files from S3 in parallel.
     * <p>
     * Callbacks can be invoked from any thread.
     *
     * @param threadCount the number of threads to set.
     */
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Set the time allowed, in seconds, for threads to shut down after
     * <code>AWSCloudTrailEventProcessingExecutor.stop()</code> is called.
     * <p>
     * Any threads still running beyond this time will be forcibly terminated.
     *
     * @param threadTerminationDelaySeconds the termination delay, in seconds, to set.
     */
    public void setThreadTerminationDelaySeconds(int threadTerminationDelaySeconds) {
        this.threadTerminationDelaySeconds = threadTerminationDelaySeconds;
    }

    /**
     * Set the maximum number of events that can be buffered per call to <code>processEvents()</code>.
     * <p>
     * Fewer events than this may be sent; this number represents only the <i>maximum</i>.
     *
     * @param maxEventsPerEmit the maximum number of events to buffer.
     */
    public void setMaxEventsPerEmit(int maxEventsPerEmit) {
        this.maxEventsPerEmit = maxEventsPerEmit;
    }

    /**
     * Set whether or not raw event information should be returned in
     * {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata}.
     *
     * @param enableRawEventInfo set to <code>true</code> to enable raw event information.
     */
    public void setEnableRawEventInfo(boolean enableRawEventInfo) {
        this.enableRawEventInfo = enableRawEventInfo;
    }
}
