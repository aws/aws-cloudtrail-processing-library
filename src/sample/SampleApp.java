/*******************************************************************************
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
                        .Builder(new SampleRecordsProcessor(), "/sample/awscloudtrailprocessinglibrary.properties")
                        .withSourceFilter(new SampleSourceFilter())
                        .withRecordFilter(new SampleRecordFilter())
                        .withProgressReporter(new SampleProgressReporter())
                        .withExceptionHandler(new SampleExceptionHandler())
                        .build();
        executor.start();

        // add shut down hook to gracefully stop exeuctor.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shut Down Hook is called.");
                executor.stop();
            }
        });

        // register a Default Uncaught Exception Handler. Shutdown hook will be called on System.exit.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Handled by global Exception handler. " + e.getMessage() + " " + t.getName());
                System.exit(1);

                //can optionally restart another executor and start.
                final AWSCloudTrailProcessingExecutor executor = new AWSCloudTrailProcessingExecutor
                        .Builder(new SampleRecordsProcessor(), "/sample/awscloudtrailprocessinglibrary.properties")
                        .withSourceFilter(new SampleSourceFilter())
                        .withRecordFilter(new SampleRecordFilter())
                        .withProgressReporter(new SampleProgressReporter())
                        .withExceptionHandler(new SampleExceptionHandler())
                        .build();
                executor.start();
            }
        });

        //can optionally limit running time, or remove both lines so it is running forever.
        Thread.sleep(24 * 60 * 60 *1000);
        executor.stop();
    }
}
