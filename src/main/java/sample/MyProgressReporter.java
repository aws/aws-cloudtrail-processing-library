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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.impl.DefaultProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;

/**
 * Simply log the processing latency.
 */
public class MyProgressReporter implements ProgressReporter {
    private static final Log logger = LogFactory.getLog(DefaultProgressReporter.class);

    @Override
    public Object reportStart(ProgressStatus status) {
        return new Date();
    }

    @Override
    public void reportEnd(ProgressStatus status, Object startDate) {
        logger.info(status.getState().toString() + " processed " + status.getStatusInfo().isSuccess() + " , and latency is " + Math.abs(((Date) startDate).getTime()-new Date().getTime()) + " milliseconds.");
    }
}
