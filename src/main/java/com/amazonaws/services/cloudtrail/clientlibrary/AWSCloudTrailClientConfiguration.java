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
package com.amazonaws.services.cloudtrail.clientlibrary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;

/**
 * AWSCloudTrailClientLibConfiguration is used to configure AWSCloudTrailRecordReader. The user either
 * pass use System properties file to load configuration or create AWSCloudTrailClientLibConfiguration 
 * object then use setter to set each attribute. If user does not provide an attribute, default value
 * will be provided.
 */
public class AWSCloudTrailClientConfiguration {
    /**
     * The URL of a SQS Queue that subscribed to CloudTrail.
     */
    public String sqsUrl = null;
    
    /**
     * The SQS region.
     */
    public String sqsRegion = DEFAULT_SQS_REGION;
    public static final String DEFAULT_SQS_REGION = "us-east-1";
    
    /**
     * A period of time during which Amazon SQS prevents other consuming components 
     * from receiving and processing that message.
     */
    public int visibilityTimeout = DEFAULT_VISIBILITY_TIMEOUT; 
    public static final int DEFAULT_VISIBILITY_TIMEOUT = 60;
    
    /**
     * The S3 end point specific to a region.
     */
    public String s3Region = DEFAULT_S3_REGION;
    public static final String DEFAULT_S3_REGION = "us-east-1";

    /**
     * Number of threads to process log files in parallel.
     */
    public int threadCount = DEFAULT_THREAD_COUNT;
    public static final int DEFAULT_THREAD_COUNT = 1;
    
    /**
     * The duration in seconds to wait for thread pool termination before issue shutDownNow.
     */
    public int threadTerminationDelay = DEFAULT_THREAD_TERMINATION_DELAY;
    public static final int DEFAULT_THREAD_TERMINATION_DELAY = 60;
    
    /**
     * Max number of AWSCloudTrailClientRecord that buffered before emit.
     */
    public int recordBufferSize = DEFAULT_RECORD_BUFFER_SIZE;
    public static final int DEFAULT_RECORD_BUFFER_SIZE = 1;

    /**
     * Whether to verify CloudTrail log file.
     */
    public boolean verifyCloudTrailLogFile = DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE;
    public static final boolean DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE = false;

    /**
     * AWS credentials provider
     */
    public AWSCredentialsProvider awsCredentialsProvider;

    /**
     * Used by lower level API and high level API to create an instance of AWSCloudTrailRecordReaderConfiguration
     * 
     * @param prop
     */
    public AWSCloudTrailClientConfiguration(String configPath) {
    	//load properties from configuration properties file
    	Properties prop = this.loadProperty(configPath);
    	
    	this.sqsUrl = prop.getProperty("sqsUrl");
    	if (this.sqsUrl == null) {
    		throw new IllegalStateException("Cannot find sqsUrl in properties file.");
    	}

    	String accessKey = prop.getProperty("accessKey");
    	String secretKey = prop.getProperty("secretKey");
    	
    	if (accessKey != null && secretKey != null) {    		
    		this.awsCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider(configPath);
    	}
        
        this.s3Region = prop.getProperty("s3Region", DEFAULT_S3_REGION);
        this.visibilityTimeout = this.getIntProperty(prop, "visibilityTimeout", DEFAULT_VISIBILITY_TIMEOUT);
        
        this.sqsRegion = prop.getProperty("s3Region", DEFAULT_SQS_REGION);

        this.threadCount = this.getIntProperty(prop, "threadCount", DEFAULT_THREAD_COUNT);
        
        this.recordBufferSize = this.getIntProperty(prop, "recordBufferSize", DEFAULT_RECORD_BUFFER_SIZE);
        this.verifyCloudTrailLogFile = this.getBooleanProperty(prop, "verifyCloudTrailLogFile", DEFAULT_VERIFY_CLOUD_TRAIL_LOG_FILE);
    }

    /**
     * Load properties from configuration stored in  
     * @param configPath
     * @return
     */
    private Properties loadProperty(String configPath) {
    	Properties prop = new Properties();
    	InputStream in = getClass().getResourceAsStream(configPath);
    	try {
    		prop.load(in);
    		in.close();
    	} catch (IOException e) {
    		throw new IllegalStateException("Cannot load property file at " + configPath, e);
    	}
    	return prop;
    }
    
	/**
	 * Parse a string representation property to integer type
	 * 
     * @param prop property class
     * @param name name used in property file
     * @param defaultBooleanValue default value assigned if cannot load value from property file
	 * @return
	 */
	private int getIntProperty(Properties prop, String name, int defaultIntValue) {
        String propertyValue = prop.getProperty(name, Integer.toString(defaultIntValue));
        return Integer.parseInt(propertyValue);
    }
	
	/**
	 * Parse a string representation property to integer type
	 * 
     * @param prop property class
     * @param name name used in property file
     * @param defaultBooleanValue default value assigned if cannot load value from property file
	 * @return
	 */
	private Boolean getBooleanProperty(Properties prop, String name, boolean defaultBooleanValue) {
        String propertyValue = prop.getProperty(name, Boolean.toString(defaultBooleanValue));
        return Boolean.parseBoolean(propertyValue);
    }
}
