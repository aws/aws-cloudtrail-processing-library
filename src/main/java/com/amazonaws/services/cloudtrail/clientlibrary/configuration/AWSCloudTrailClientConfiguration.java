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
package com.amazonaws.services.cloudtrail.clientlibrary.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * AWSCloudTrailClientConfiguration is used to configure AWSCloudTrailClientRecordReader. The user either
 * pass use System properties file to load configuration or create AWSCloudTrailClientConfiguration
 * object then use setter to set each attribute. If user does not provide an attribute, default value
 * will be provided.
 */
public interface AWSCloudTrailClientConfiguration {
    /**
     * default configuration value
     */
    public static final String DEFAULT_SQS_REGION = "us-east-1";
    public static final String DEFAULT_S3_REGION = "us-east-1";
    public static final int DEFAULT_VISIBILITY_TIMEOUT = 60;
    public static final int DEFAULT_THREAD_COUNT = 1;
    public static final int DEFAULT_THREAD_TERMINATION_DELAY = 60;
    public static final int DEFAULT_N_RECORDS_PER_EMIT = 1;
    public static final boolean DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE = false;
    public static final boolean DEFAULT_ENABLE_RAW_RECORD_INFO = false;

    /**
     * @return the sqsUrl
     */
    public String getSqsUrl();

    /**
     * @return the sqsRegion
     */
    public String getSqsRegion();

    /**
     * @return the visibilityTimeout
     */
    public int getVisibilityTimeout();

    /**
     * @return the s3Region
     */
    public String getS3Region();

    /**
     * @return the threadCount
     */
    public int getThreadCount();

    /**
     * @return the threadTerminationDelay
     */
    public int getThreadTerminationDelay();

    /**
     * @return the nRecordsPerEmit
     */
    public int getNRecordsPerEmit();

    /**
     * @return enableRawRecordInfo
     */
    public boolean isEnableRawRecordInfo();

    /**
     * @return the awsCredentialsProvider
     */
    public AWSCredentialsProvider getAwsCredentialsProvider();

    /**
     * Validate the required parameters are set in configuration
     */
    public void validate();
}
