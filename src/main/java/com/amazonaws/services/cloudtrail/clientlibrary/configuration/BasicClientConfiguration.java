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
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;

public class BasicClientConfiguration implements AWSCloudTrailClientConfiguration{

    private static final String ERROR_CREDENTIALS_PROVIDER_NULL = "CredentialsProvider is null. Either put your " +
            "access key and secret key in configuration file in your class path, or set it in the " +
            "configuration object you passed in.";

    /**
     * The URL of a SQS Queue that subscribed to CloudTrail.
     */
    public String sqsUrl = null;

    /**
     * AWS credentials provider
     */
    public AWSCredentialsProvider awsCredentialsProvider;

    /**
     * The SQS region.
     */
    public String sqsRegion = DEFAULT_SQS_REGION;

    /**
     * A period of time during which Amazon SQS prevents other consuming components
     * from receiving and processing that message.
     */
    public int visibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT;

    /**
     * The S3 end point specific to a region.
     */
    public String s3Region = DEFAULT_S3_REGION;


    /**
     * Number of threads to process log files in parallel.
     */
    public int threadCount = DEFAULT_THREAD_COUNT;


    /**
     * The duration in seconds to wait for thread pool termination before issue shutDownNow.
     */
    public int threadTerminationDelay = DEFAULT_THREAD_TERMINATION_DELAY;


    /**
     * Max number of AWSCloudTrailClientRecord that buffered before emit.
     */
    public int nRecordsPerEmit = DEFAULT_N_RECORDS_PER_EMIT;

    /**
     * Whether to verify CloudTrail log file's signature.
     */
    public boolean verifyCloudTrailLogFile = DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE;

    /**
     * Whether to enable raw record in CloudTrailDeliveryInfo
     */
    public boolean enableRawRecordInfo = DEFAULT_ENABLE_RAW_RECORD_INFO;

    public BasicClientConfiguration(String sqsUrl, AWSCredentialsProvider awsCredentialsProvider) {
        this.sqsUrl = sqsUrl;
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    /**
     * @return the sqsUrl
     */
    public String getSqsUrl() {
        return sqsUrl;
    }

    /**
     * @return the sqsRegion
     */
    public String getSqsRegion() {
        return sqsRegion;
    }

    /**
     * @return the visibilityTimeout
     */
    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * @return the s3Region
     */
    public String getS3Region() {
        return s3Region;
    }

    /**
     * @return the threadCount
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * @return the threadTerminationDelay
     */
    public int getThreadTerminationDelay() {
        return threadTerminationDelay;
    }

    /**
     * @return the nRecordsPerEmit
     */
    public int getNRecordsPerEmit() {
        return nRecordsPerEmit;
    }

    @Override
    public boolean isEnableRawRecordInfo() {
        return enableRawRecordInfo;
    }

    /**
     * @return the awsCredentialsProvider
     */
    public AWSCredentialsProvider getAwsCredentialsProvider() {
        return awsCredentialsProvider;
    }

    @Override
    public void validate() {
        ClientLibraryUtils.checkArgumentNotNull(this.getAwsCredentialsProvider(), ERROR_CREDENTIALS_PROVIDER_NULL);
        ClientLibraryUtils.checkArgumentNotNull(this.getSqsUrl(), "SQS URL is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getNRecordsPerEmit(),  "Credential is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getS3Region(),  "S3Region is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getSqsRegion(),  "sqsRegion is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getSqsUrl(),  "sqsUrl is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getThreadCount(),  "threadCount is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getThreadTerminationDelay(),  "threadTerminationDelay is null.");
        ClientLibraryUtils.checkArgumentNotNull(this.getVisibilityTimeout(),  "visibilityTimeout is null.");
    }

    /**
     * @param sqsRegion the sqsRegion to set
     */
    public void setSqsRegion(String sqsRegion) {
        this.sqsRegion = sqsRegion;
    }

    /**
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
     * @param threadTerminationDelay the threadTerminationDelay to set
     */
    public void setThreadTerminationDelay(int threadTerminationDelay) {
        this.threadTerminationDelay = threadTerminationDelay;
    }

    /**
     * @param nRecordsPerEmit the nRecordsPerEmit to set
     */
    public void setnRecordsPerEmit(int nRecordsPerEmit) {
        this.nRecordsPerEmit = nRecordsPerEmit;
    }

    /**
     * @param verifyCloudTrailLogFile the verifyCloudTrailLogFile to set
     */
    public void setVerifyCloudTrailLogFile(boolean verifyCloudTrailLogFile) {
        this.verifyCloudTrailLogFile = verifyCloudTrailLogFile;
    }

    /**
     * @param enableRawRecordInfo the enableRawRecordInfo to set
     */
    public void setEnableRawRecordInfo(boolean enableRawRecordInfo) {
        this.enableRawRecordInfo = enableRawRecordInfo;
    }

    /**
     * @param awsCredentialsProvider the awsCredentialsProvider to set
     */
    public void setAwsCredentialsProvider(AWSCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

}