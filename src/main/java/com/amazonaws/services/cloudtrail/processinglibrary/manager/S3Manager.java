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

package com.amazonaws.services.cloudtrail.processinglibrary.manager;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicProcessLogInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Manages Amazon S3 service-related operations.
 */
public class S3Manager {
    private static final Log logger = LogFactory.getLog(SqsManager.class);

    private AmazonS3 s3Client;
    private ProcessingConfiguration config;
    private ExceptionHandler exceptionHandler;
    private ProgressReporter progressReporter;

    /**
     * S3Manager constructor.
     *
     * @param s3Client A {@link AmazonS3}.
     * @param config A {@link ProcessingConfiguration}.
     * @param exceptionHandler An implementation of {@link ExceptionHandler} used to handle errors.
     * @param progressReporter An implementation of {@link ProgressReporter} used to report progress.
     */
    public S3Manager(AmazonS3 s3Client,
                     ProcessingConfiguration config,
                     ExceptionHandler exceptionHandler,
                     ProgressReporter progressReporter) {
        this.config = config;
        this.exceptionHandler = exceptionHandler;
        this.progressReporter = progressReporter;
        this.s3Client = s3Client;

        validate();
    }

    /**
     * Downloads an AWS CloudTrail log from the specified source.
     *
     * @param ctLog The {@link CloudTrailLog} to download
     * @param source The {@link CloudTrailSource} to download the log from.
     * @return A byte array containing the log data.
     */
    public byte[] downloadLog(CloudTrailLog ctLog, CloudTrailSource source) {
        boolean success = false;
        ProgressStatus downloadLogStatus = new ProgressStatus(ProgressState.downloadLog, new BasicProcessLogInfo(source, ctLog, success));
        final Object downloadSourceReportObject = progressReporter.reportStart(downloadLogStatus);

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
            String exceptionMessage = String.format("Fail to download log file %s/%s.", ctLog.getS3Bucket(), ctLog.getS3ObjectKey());
            LibraryUtils.handleException(exceptionHandler, downloadLogStatus, e, exceptionMessage);

        } finally {
            LibraryUtils.endToProcess(progressReporter, success, downloadLogStatus, downloadSourceReportObject);
        }

        return s3ObjectBytes;
    }

    /**
     * Download an S3 object.
     *
     * @param bucketName The S3 bucket name from which to download the object.
     * @param objectKey The S3 key name of the object to download.
     * @return The downloaded
     *     <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/S3Object.html">S3Object</a>.
     */
    public S3Object getObject(String bucketName, String objectKey) {
        try {
            return s3Client.getObject(bucketName, objectKey);
        } catch (AmazonServiceException e) {
            logger.error("Failed to get object " + objectKey + " from s3 bucket " + bucketName);
            throw e;
        }
    }

    /**
     * Validates input parameters.
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(config, "configuration is null");
        LibraryUtils.checkArgumentNotNull(exceptionHandler, "exceptionHandler is null");
        LibraryUtils.checkArgumentNotNull(progressReporter, "progressReporter is null");
        LibraryUtils.checkArgumentNotNull(s3Client, "s3Client is null");
    }
}
