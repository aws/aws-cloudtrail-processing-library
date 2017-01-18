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

package com.amazonaws.services.cloudtrail.processinglibrary.model;

/**
 * This a log that AWS CloudTrail published to user's SNS topic.
 */
public class CloudTrailLog {
    /**
     * The S3 bucket where log files are stored
     */
    private final String s3Bucket;

    /**
     * S3 object keys
     */
    private String s3ObjectKey;

    /**
     * The CloudTrail log file size
     */
    private long logFileSize;

    /**
     * Constructs a new CloudTrailLog object.
     *
     * @param s3Bucket The S3 bucket where log files are stored
     * @param s3ObjectKey The S3 object key
     */
    public CloudTrailLog(String s3Bucket, String s3ObjectKey) {
        this.s3Bucket = s3Bucket;
        this.s3ObjectKey = s3ObjectKey;
    }

    /**
     * AWS S3 bucket name
     * @return AWS S3 bucket name
     */
    public String getS3Bucket() {
        return s3Bucket;
    }

    /**
     * S3 object key in a single SQS message.
     * @return S3 object keys in a single SQS message.
     */
    public String getS3ObjectKey() {
        return s3ObjectKey;
    }

    /**
     * CloudTrail log File size in bytes
     * @return CloudTrail log file size
     */
    public long getLogFileSize() {
        return logFileSize;
    }

    /**
     * Set Log file size when retrieve this information from S3 metadata
     * @param logFileSize
     */
    public void setLogFileSize(long logFileSize) {
        this.logFileSize = logFileSize;
    }

    /**
     * Returns a string representation of this object; useful for testing and debugging.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CloudTrailLog [");
        if (s3Bucket != null) {
            builder.append("s3Bucket=");
            builder.append(s3Bucket);
            builder.append(", ");
        }
        if (s3ObjectKey != null) {
            builder.append("s3ObjectKey=");
            builder.append(s3ObjectKey);
            builder.append(", ");
        }
        builder.append("logFileSize=");
        builder.append(logFileSize);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (logFileSize ^ (logFileSize >>> 32));
        result = prime * result
                + ((s3Bucket == null) ? 0 : s3Bucket.hashCode());
        result = prime * result
                + ((s3ObjectKey == null) ? 0 : s3ObjectKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CloudTrailLog other = (CloudTrailLog) obj;
        if (logFileSize != other.logFileSize)
            return false;
        if (s3Bucket == null) {
            if (other.s3Bucket != null)
                return false;
        } else if (!s3Bucket.equals(other.s3Bucket))
            return false;
        if (s3ObjectKey == null) {
            if (other.s3ObjectKey != null)
                return false;
        } else if (!s3ObjectKey.equals(other.s3ObjectKey))
            return false;
        return true;
    }
}
