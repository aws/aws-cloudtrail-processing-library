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
package sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientExecutor;

/**
 * Sample App build on top of AWS CloudTrail client library
 */
public class App {
	private final static Log logger = LogFactory.getLog(App.class);
	
    public static void main( String[] args ) throws InterruptedException{
        logger.info("Begining sample code in brazil.");
		
        AWSCloudTrailClientExecutor executor = new AWSCloudTrailClientExecutor("/sample/awscloudtrailclientlib.properties");
        executor.setRecordsEmitter(new MyLogEmitter());
        executor.setProgressReporter(new MyProgressReporter());
        executor.start();
        Thread.sleep(20*1000);
        executor.stop();
    }
}
