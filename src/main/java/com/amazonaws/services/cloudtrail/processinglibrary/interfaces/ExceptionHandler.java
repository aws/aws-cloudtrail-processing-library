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

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader;
import com.amazonaws.services.sqs.model.Message;

import java.util.List;

/**
 * Provides a callback function that handles exceptions that occurred while processing AWS CloudTrail log files.
 * <p>
 * The {@link #handleException(ProcessingLibraryException)} method is invoked when exceptions are raised in the following scenarios when:
 * </p>
 * <ol>
 *   <li>Polling messages from SQS - {@link SqsManager#pollQueue()}.</li>
 *   <li>Parsing message from SQS - {@link SqsManager#parseMessage(List)}</li>
 *   <li>Deleting messages from SQS - {@link SqsManager#deleteMessageFromQueue(Message, ProgressStatus)}.</li>
 *   <li>Downloading an AWS CloudTrail log file - {@link S3Manager#downloadLog(CloudTrailLog, CloudTrailSource)}.</li>
 *   <li>Processing the AWS CloudTrail log file - {@link EventReader#processSource(CloudTrailSource)}.</li>
 *   <li>Any uncaught exceptions.</li>
 * </ol>
 * <p>
 * A {@link ProcessingLibraryException} contains execution context in the held {@link ProgressStatus} object,
 * which can be obtained by calling {@link ProcessingLibraryException#getStatus()}.
 * </p>
 */
public interface ExceptionHandler {

    /**
     * A callback method that handles exceptions that occurred while processing AWS CloudTrail log files.
     *
     * @param exception A {@link ProcessingLibraryException}.
     */
    public void handleException(ProcessingLibraryException exception);
}
