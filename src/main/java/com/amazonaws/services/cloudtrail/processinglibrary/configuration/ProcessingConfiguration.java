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

/**
 * Data used to configure a {@link RecordReader}.
 * <p>
 * The user either passes a system properties file to load the configuration or
 * creates a BasicProcessingConfiguration object then use setter to set
 * each attribute. If user does not provide an attribute, default value will be
 * provided.
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
     * The default number of records accumulated before emitting; {@value}.
     */
    public static final int DEFAULT_MAX_RECORDS_PER_EMIT = 1;
    /**
     * Whether to verify cloud trail log files by default; {@value}.
     */
    public static final boolean DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE = false;
    /**
     * Whether to enable raw record information in record data; {@value}.
     */
    public static final boolean DEFAULT_ENABLE_RAW_RECORD_INFO = false;

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
     * Get the number of records per emit.
     *
     * @return the maxRecordsPerEmit
     */
    public int getMaxRecordsPerEmit();

    /**
     * Indicates if raw record information is returned in {@CloudTrailDeliveryInfo}.
     *
     * @return <code>true</code> if raw record information is enabled;
     *   <code>false</code> otherwise.
     */
    public boolean isEnableRawRecordInfo();

    /**
     * Get the AWS Credentials provider used to verify logs.
     *
     * @return an {@AWSCredentialsProvider} object.
     */
    public AWSCredentialsProvider getAwsCredentialsProvider();

    /**
     * Validate that all necessary parameters are set in the provided
     * configuration.
     * <p>
     * This method throws an exception if any required parameters are
     * <code>null</code>.
     *
     * @throws IllegalStateException
     */
    public void validate();
}
