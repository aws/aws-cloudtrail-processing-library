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
package com.amazonaws.services.cloudtrail.clientlibrary.interfaces;

import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;

/**
 * ProgressReporter is a call back function that report execution progress.
 * The reportStart() and reportEnd() methods are invoked in at beginning and the end of:
 *
 * 1. Polling messages from SQS
 * 2. Parsing message from SQS
 * 3. Deleting messages from SQS
 * 4. Downloading CloudTrail log file
 * 5. Parsing CloudTrail log file
 *
 * PorgressStatus provides different informations.
 * See {@link com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus} for more information.
 */
public interface ProgressReporter {
    public Object reportStart(ProgressStatus status);
    public void reportEnd(ProgressStatus status, Object object);
}
