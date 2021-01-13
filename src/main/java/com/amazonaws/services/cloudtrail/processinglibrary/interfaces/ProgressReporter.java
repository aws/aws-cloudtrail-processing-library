/*******************************************************************************
 * Copyright 2010-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader;
import com.amazonaws.services.sqs.model.Message;

import java.util.List;

/**
 * <code>ProgressReporter</code> is an interface for providing custom handlers of AWS CloudTrail Processing Library
 * progress.
 *<p>
 * {@link #reportStart(ProgressStatus)} and {@link #reportEnd(ProgressStatus, Object)} are invoked at the beginning and
 * the end of the following actions:
 * </p>
 * <ol>
 *   <li>Polling messages from SQS - {@link SqsManager#pollQueue()}.</li>
 *   <li>Parsing message from SQS - {@link SqsManager#parseMessage(List)}.</li>
 *   <li>Deleting messages from SQS - {@link SqsManager#deleteMessageFromQueue(Message, ProgressStatus)}.</li>
 *   <li>Downloading an AWS CloudTrail log file - {@link S3Manager#downloadLog(CloudTrailLog, CloudTrailSource)}.</li>
 *   <li>Processing the AWS CloudTrail log file - {@link EventReader#processSource(CloudTrailSource)}.</li>
 * </ol>
 *
 * @see ProgressStatus for more information.
 */
public interface ProgressReporter {
    /**
     * A callback method that report starting status.
     *
     * @param status A {@link ProgressStatus} that represents the status of the current action being performed.
     * @return An {@link Object} that can be sent to {@link #reportEnd(ProgressStatus, Object)}.
     */
    public Object reportStart(ProgressStatus status);

    /**
     * A callback method that report ending status.
     *
     * @param status A {@link ProgressStatus} that represents the status of the current action being performed.
     * @param object An {@link Object} to send; usually the object returned by {{@link #reportStart(ProgressStatus)}}.
     */
    public void reportEnd(ProgressStatus status, Object object);
}
