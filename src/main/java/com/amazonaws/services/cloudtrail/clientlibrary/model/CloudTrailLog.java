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
package com.amazonaws.services.cloudtrail.clientlibrary.model;



/**
 * This an source that AWS CloudTrail published to users' topic.
 */
public class CloudTrailLog {
    
    /**
     * AWSCloudTrailSource's signature verification state.
     */
    private SignatureVerificationName signatureVerification;
    
    /**
     * The S3 bucket where log files are stored
     */
    private final String s3Bucket;
    
    /**
     * S3 object keys
     */
    private String s3ObjectKey;
    
    /**
     * Constructs a new AWSCloudTrailSource object.
     * 
     * @param accountId AWS Account Id
     * @param s3Bucket The S3 bucket where log files are stored
     * @param s3ObjectKey The S3 object key
     * @param handle An identifier associated with the act of receiving a SQS message
     */
    public CloudTrailLog(String s3Bucket, String s3ObjectKey) {
        this.s3Bucket = s3Bucket;
        this.s3ObjectKey = s3ObjectKey;
        this.signatureVerification = SignatureVerificationName.SignatureNotVerified;
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
	 * @return the signatureVerification
	 */
	public SignatureVerificationName getSignatureVerification() {
		return signatureVerification;
	}

	/**
	 * @param signatureVerification the signatureVerification to set
	 */
	public void setSignatureVerification(
			SignatureVerificationName signatureVerification) {
		this.signatureVerification = signatureVerification;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "CloudTrailLog [signatureVerification=" + signatureVerification
				+ ", s3Bucket=" + s3Bucket + ", s3ObjectKey=" + s3ObjectKey
				+ "]";
	}
}
