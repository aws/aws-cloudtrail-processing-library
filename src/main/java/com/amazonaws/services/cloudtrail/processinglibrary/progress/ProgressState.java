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

package com.amazonaws.services.cloudtrail.processinglibrary.progress;

/**
 * CloudTrail progress state.
 */
public enum ProgressState {
    /**
     * Report progress when polling messages from SQS queue
     */
    pollQueue,

    /**
     * Report progress when parsing a message from SQS queue.
     */
    parseMessage,

    /**
     * Report progress when deleting a message from SQS queue.
     */
    deleteMessage,

    /**
     * Report progress when deleting a filtered out message from SQS queue.
     */
    deleteFilteredMessage,

    /**
     * Report progress when processing source
     */
    processSource,

    /**
     * Report progress when downloading log file
     */
    downloadLog,

    /**
     * Report progress when processing log file
     */
    processLog,

    /**
     * Report progress when uncaught exception happened
     */
    uncaughtException
}
