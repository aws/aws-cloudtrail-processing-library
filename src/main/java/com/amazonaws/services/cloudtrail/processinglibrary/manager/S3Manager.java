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

package com.amazonaws.services.cloudtrail.processinglibrary.manager;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicProcessLogInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Manages Amazon S3 service-related operations.
 */
public class S3Manager {
    private static final Log logger = LogFactory.getLog(SqsManager.class);

    private AmazonS3Client s3Client;
    private ProcessingConfiguration config;
    private ExceptionHandler exceptionHandler;
    private ProgressReporter progressReporter;

    /**
     * S3Manager constructor
     *
     * @param s3Client the S3 client to use.
     * @param configuration a
     *     {@link com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration}.
     * @param exceptionHandler an implementation of
     *     {@link com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler}, used to handle errors.
     * @param progressReporter an implementation of
     *     {@link com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter}, used to report progress.
     */
    public S3Manager(AmazonS3Client s3Client, ProcessingConfiguration configuration, ExceptionHandler exceptionHandler, ProgressReporter progressReporter) {
        this.config = configuration;
        this.exceptionHandler = exceptionHandler;
        this.progressReporter = progressReporter;
        this.s3Client = s3Client;
        this.validate();
    }

    /**
     * Downloads an AWS CloudTrail log from the specified source.
     *
     * @param ctLog the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog} to download
     * @param source the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource} to download
     *     the log from.
     * @return a byte array containing the log data.
     */
    public byte[] downloadLog(CloudTrailLog ctLog, CloudTrailSource source) {
        boolean success = false;
        ProgressStatus startStatus = new ProgressStatus(ProgressState.downloadLog, new BasicProcessLogInfo(source, ctLog, success));
        final Object downloadSourceReportObject = this.progressReporter.reportStart(startStatus);

        byte[] s3ObjectBytes = null;

        // start to download CloudTrail log
        try {
            S3Object s3Object = this.getObject(ctLog.getS3Bucket(), ctLog.getS3ObjectKey());
            try (S3ObjectInputStream s3InputStream = s3Object.getObjectContent()){
                s3ObjectBytes = LibraryUtils.toByteArray(s3InputStream);
            }
            ctLog.setLogFileSize(s3Object.getObjectMetadata().getContentLength());
            success = true;
            logger.info("Downloaded log file " + ctLog.getS3ObjectKey() + " from " + ctLog.getS3Bucket());

        } catch (AmazonServiceException | IOException e) {

            ProcessingLibraryException exception = new ProcessingLibraryException("Fail to download log file.", e, startStatus);
            this.exceptionHandler.handleException(exception);

        } finally {
            ProgressStatus endStatus = new ProgressStatus(ProgressState.downloadLog, new BasicProcessLogInfo(source, ctLog, success));
            this.progressReporter.reportEnd(endStatus, downloadSourceReportObject);
        }

        return s3ObjectBytes;
    }

    /**
     * Download an S3 object.
     *
     * @param bucketName the S3 bucket name from which to download the object.
     * @param objectKey the S3 key name of the object to download.
     * @return the downloaded
     *     <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/S3Object.html">S3Object</a>.
     */
    public S3Object getObject(String bucketName, String objectKey) {
        try {
            return this.s3Client.getObject(bucketName, objectKey);
        } catch (AmazonServiceException e) {
            logger.error("Failed to get object " + objectKey + " from s3 bucket " + bucketName);
            throw e;
        }
    }

    /**
     * Validates input parameters.
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(this.config, "configuration is null");
        LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exception handler is null");
        LibraryUtils.checkArgumentNotNull(this.progressReporter, "progress reporter is null");
        LibraryUtils.checkArgumentNotNull(this.s3Client, "s3 client is null");
    }
}
