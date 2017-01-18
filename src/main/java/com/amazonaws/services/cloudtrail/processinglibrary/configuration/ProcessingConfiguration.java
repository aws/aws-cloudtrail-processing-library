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

package com.amazonaws.services.cloudtrail.processinglibrary.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * Data used to configure a {@link com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader}.
 * <p>
 * You can use a system properties file to load the configuration or create a {@link ClientConfiguration} object and set
 * each attribute. If you do not provide a value for an attribute, a default value will be provided.
 */
public interface ProcessingConfiguration {
    /* default configuration values */

    /**
     * The default SQS region; {@value}.
     */
    public static final String DEFAULT_SQS_REGION = "us-east-1";

    /**
     * The default S3 region; {@value}.
     */
    public static final String DEFAULT_S3_REGION = "us-east-1";

    /**
     * The default SQS visibility timeout, in seconds; {@value}.
     */
    public static final int DEFAULT_VISIBILITY_TIMEOUT = 60;

    /**
     * The default S3 thread count; {@value}.
     */
    public static final int DEFAULT_THREAD_COUNT = 1;

    /**
     * The default thread termination delay, in seconds; {@value}.
     */
    public static final int DEFAULT_THREAD_TERMINATION_DELAY_SECONDS = 60;

    /**
     * The default number of events accumulated before emitting; {@value}.
     */
    public static final int DEFAULT_MAX_EVENTS_PER_EMIT = 1;

    /**
     * Whether to enable raw event information in event metadata; {@value}.
     */
    public static final boolean DEFAULT_ENABLE_RAW_EVENT_INFO = false;

    /**
     * Get the AWS Credentials provider used to access AWS.
     *
     * @return an
     *     <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/AWSCredentialsProvider.html">AWSCredentialsProvider</a>
     *     object.
     */
    public AWSCredentialsProvider getAwsCredentialsProvider();

    /**
     * Gets the SQS URL used to obtain CloudTrail logs.
     *
     * @return the configured SQS URL used to get CloudTrail logs.
     */
    public String getSqsUrl();

    /**
     * Gets the SQS Region from which CloudTrail logs are obtained.
     *
     * @return the SQS Region
     */
    public String getSqsRegion();

    /**
     * Get the visibility timeout value for the SQS queue.
     * <p>
     * The period of time during which Amazon SQS prevents other consuming
     * components from receiving and processing a message.
     *
     * @return the visibility timeout value.
     */
    public int getVisibilityTimeout();

    /**
     * Get the AWS S3 Region.
     *
     * @return the Amazon S3 region used.
     */
    public String getS3Region();

    /**
     * Get the number of threads used to download S3 files in parallel.
     *
     * @return the number of threads.
     */
    public int getThreadCount();

    /**
     * Get the thread termination delay value.
     *
     * @return the thread termination delay, in seconds.
     */
    public int getThreadTerminationDelaySeconds();

    /**
     * Get the maximum number of AWSCloudTrailClientEvents sent to a single invocation of processEvents().
     *
     * @return the maximum number of events that will be buffered per call to processEvents.
     */
    public int getMaxEventsPerEmit();

    /**
     * Indicates if raw event information will be returned in
     * {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata}.
     *
     * @return <code>true</code> if raw event information is enabled; <code>false</code> otherwise.
     */
    public boolean isEnableRawEventInfo();

    /**
     * Validate that all necessary parameters are set in the provided configuration.
     * <p>
     * This method throws an exception if any of the required parameters are <code>null</code>.
     *
     * @throws IllegalStateException if any parameters are <code>null</code>.
     */
    public void validate();
}
