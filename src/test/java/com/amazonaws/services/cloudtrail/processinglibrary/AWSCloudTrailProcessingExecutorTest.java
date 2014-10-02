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
package com.amazonaws.services.cloudtrail.processinglibrary;

import java.util.List;

import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.BasicProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;

public class AWSCloudTrailProcessingExecutorTest {

    RecordsProcessor myProcessor = new RecordsProcessor() {
        @Override
        public void process(List<CloudTrailClientRecord> records) {
            // doNothing
        }
    };


    /**
     * No sqsUrl in property file.
     */
    @Test
    public void testConstructExecutorUsingPropertyFile() {
        new AWSCloudTrailProcessingExecutor.Builder(myProcessor, "/resources/Good.properties").build();
    }

    /**
     * No sqsUrl in property file.
     */
    @Test (expected = IllegalStateException.class)
    public void testConstructExecutorUsingPropertyFileNoSQSUrl() {
        new AWSCloudTrailProcessingExecutor.Builder(myProcessor, "/resources/NoSqsUrls.properties").build();
    }

    /**
     * No credentials in property file.
     */
    @Test (expected = IllegalStateException.class)
    public void testConstructExecutorUsingPropertyFileNoCredentials() {
        new AWSCloudTrailProcessingExecutor.Builder(myProcessor, "/resources/NoCredentials.properties").build();
    }

    /**
     * No sqsUrl in property file.
     */
    @Test
    public void testPassInConfiguration() {
        AWSCredentials credentials = new BasicAWSCredentials("accessKey", "secretKey");
        AWSCredentialsProvider credentialsProvider = new StaticCredentialsProvider(credentials);
        new AWSCloudTrailProcessingExecutor.Builder(myProcessor, new BasicProcessingConfiguration("MySQSUrl", credentialsProvider)).build();
    }
}
