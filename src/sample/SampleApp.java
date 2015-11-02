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

package sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor;

/**
 * Sample application that use AWS CloudTrail Processing Library
 */
public class SampleApp {
    public static void main(String[] args) throws InterruptedException{
        final Log logger = LogFactory.getLog(SampleApp.class);

        //create AWSCloudTrailProcessingExecutor and start it
        final AWSCloudTrailProcessingExecutor executor = new AWSCloudTrailProcessingExecutor
                        .Builder(new SampleEventsProcessor(), "/sample/awscloudtrailprocessinglibrary.properties")
                        .withSourceFilter(new SampleSourceFilter())
                        .withEventFilter(new SampleEventFilter())
                        .withProgressReporter(new SampleProgressReporter())
                        .withExceptionHandler(new SampleExceptionHandler())
                        .build();
        executor.start();

        // add shut down hook to gracefully stop executor (optional)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shut Down Hook is called.");
                executor.stop();
            }
        });

        // register a Default Uncaught Exception Handler (optional)
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Handled by global Exception handler. " + e.getMessage() + " " + t.getName());

                //Two options here:
                //First, we can call System.exit(1); in such case shut down hook will be called.
                //Second, we can optionally restart another executor and start.
                final AWSCloudTrailProcessingExecutor executor = new AWSCloudTrailProcessingExecutor
                        .Builder(new SampleEventsProcessor(), "/sample/awscloudtrailprocessinglibrary.properties")
                        .withSourceFilter(new SampleSourceFilter())
                        .withEventFilter(new SampleEventFilter())
                        .withProgressReporter(new SampleProgressReporter())
                        .withExceptionHandler(new SampleExceptionHandler())
                        .build();
                executor.start();
            }
        });

        //can optionally limit running time, or remove both lines so it is running forever. (optional)
        Thread.sleep(24 * 60 * 60 *1000);
        executor.stop();
    }
}
