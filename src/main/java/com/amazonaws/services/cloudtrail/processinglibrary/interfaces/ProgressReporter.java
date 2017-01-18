/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.amazonaws.services.cloudtrail.processinglibrary.interfaces;

import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;

/**
 * ProgressReporter is an interface that can be used to provide custom handling of AWS CloudTrail Processing Library
 * progress.
 *
 * reportStart() and reportEnd() are invoked at the beginning and the end of the following actions:
 *
 * 1. Polling messages from SQS
 * 2. Parsing message from SQS
 * 3. Processing an Amazon SQS source for CloudTrail logs
 * 4. Deleting messages from SQS (filtered and unfiltered)
 * 5. Downloading CloudTrail log file
 * 6. Processing CloudTrail log file
 *
 * See {@link com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus} for more information.
 */
public interface ProgressReporter {
    /**
     * A callback method that report starting status.
     *
     * @param status a {@link com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus} that
     *     represents the status of the current action being performed.
     * @return an Object that can be sent to <code>reportEnd()</code>.
     */
    public Object reportStart(ProgressStatus status);

    /**
     * A callback method that report ending status.
     *
     * @param status a {@link com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus} that
     *     represents the status of the current action being performed.
     * @param object an object to send; usually the object returned by <code>reportStart()</code>.
     */
    public void reportEnd(ProgressStatus status, Object object);
}
