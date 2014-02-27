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
package com.amazonaws.services.cloudtrail.clientlibrary.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * A convenient class to manage Amazon S3 Service related operations.
 */
public class S3Manager {
    private static final Log logger = LogFactory.getLog(SqsManager.class);
    
    /**
     * An instance of AmazonS3Client.
     */
    private AmazonS3Client s3Client;
    
    /**
     * An instance of AWSCloudTrailClientConfiguration
     */
    private AWSCloudTrailClientConfiguration config;

    /**
     * S3Manager constructor
     * @param configuration
     */
    public S3Manager(AWSCloudTrailClientConfiguration configuration) {
        this.config = configuration;
        validate();
        
        this.s3Client = new AmazonS3Client(configuration.awsCredentialsProvider);
//        this.s3Client.setEndpoint(this.config.s3EndPoint);
        this.s3Client.setRegion(Region.getRegion(Regions.fromName(this.config.s3Region)));
    }

    /**
     * API call to Amazon S3 service to download a S3 object
     * 
     * @param bucketName
     * @param objectKey
     * @return
     */
    public S3Object getObject(String bucketName, String objectKey) {
    	S3Object s3Object = null;
        try {
            
            s3Object = this.s3Client.getObject(bucketName, objectKey);
            
        } catch (AmazonServiceException e) {
            logger.error("Failed to get object " + objectKey + " from s3 bucket " + bucketName);
            throw e;
        }
        
        return s3Object;
    }
    
    /**
     * Open an inputStream object from a S3 object.
     * 
     * @param bucketName
     * @param objectKey
     * @return
     * @throws IOException
     */
    public InputStream openLogFile(S3Object s3Object) throws IOException {
        S3ObjectInputStream s3ObjectInputStream = null;
        try {
            
            s3ObjectInputStream = s3Object.getObjectContent();
            
        } catch (AmazonServiceException e) {
            logger.error("Failed to download log file object " + s3Object.getKey() + " from s3 bucket " + s3Object.getBucketName());
            throw e;
        } 
        
        return new GZIPInputStream(s3ObjectInputStream);
    }
    
    /**
     * Convenient function to validate input
     */
    private void validate() {
    	if (this.config == null) {
    		throw new IllegalArgumentException("Invalide state of " + this.getClass().getName() + ": configuration is null");
    	}
    }
}
