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

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Manages Amazon S3 service-related operations.
 */
public interface S3Manager {

    /**
     * Downloads an AWS CloudTrail log from the specified source.
     *
     * @param ctLog The {@link CloudTrailLog} to download
     * @param source The {@link CloudTrailSource} to download the log from.
     * @return A byte array containing the log data.
     */
    byte[] downloadLog(CloudTrailLog ctLog, CloudTrailSource source);

    /**
     * Download an S3 object.
     *
     * @param bucketName The S3 bucket name from which to download the object.
     * @param objectKey The S3 key name of the object to download.
     * @return The downloaded
     *     <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/S3Object.html">S3Object</a>.
     */
    S3Object getObject(String bucketName, String objectKey);
}
