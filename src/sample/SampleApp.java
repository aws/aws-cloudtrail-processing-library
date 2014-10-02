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

import com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor;

/**
 * Sample app build on top of AWS CloudTrail client library
 */
public class SampleApp {
    public static void main(String[] args) throws InterruptedException{
        final Log logger = LogFactory.getLog(SampleApp.class);

        final AWSCloudTrailProcessingExecutor executor = new AWSCloudTrailProcessingExecutor.Builder(new SampleLogProcessor(), "/sample/awscloudtrailprocessinglibrary.properties")
                        .withProgressReporter(new SampleProgressReporter())
                        .build();
        executor.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shut Down Hook is called.");
                executor.stop();
            }
        });

        // Register a Default Uncaught Exception Handler. Shutdown hook will be called on System.exit.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Handled by global Exception handler. " + e.getMessage() + " " + t.getName());
                System.exit(1);

                //Can optionally restart another executor and start
//                AWSCloudTrailClientExecutor executor = new AWSCloudTrailClientExecutor.Builder(new MyLogProcessor(), "/sample/awscloudtrailprocessinglibrary.properties")
//                        .withProgressReporter(new MyProgressReporter())
//                        .build();
//                executor.start();
            }
        });

//        Thread.sleep(60*1000);
//        executor.stop();
    }
}
