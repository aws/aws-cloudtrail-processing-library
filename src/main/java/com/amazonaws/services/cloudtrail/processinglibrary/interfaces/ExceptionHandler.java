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

package com.amazonaws.services.cloudtrail.processinglibrary.interfaces;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException;

/**
 * Provides a callback function that handles exceptions that occurred while
 * processing AWS CloudTrail log files.
 * <p>
 * The <code>handleException()</code> method is invoked when exceptions are
 * raised in these cases:
 * <p>
 * <ol>
 *   <li>while polling messages from SQS</li>
 *   <li>while parsing message from SQS</li>
 *   <li>while deleting messages from SQS</li>
 *   <li>while downloading an AWS CloudTrail log file</li>
 *   <li>while processing the AWS CloudTrail log file</li>
 *   <li>any uncaught exception</li>
 * </ol>
 * <p>
 * A {@link com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException} contains
 * execution context in the held {@link com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus}
 * object, which can be obtained by calling the exception's <code>getStatus()</code> method.
 */
public interface ExceptionHandler {

    /**
     * A callback method that handles exceptions that occurred while processing AWS CloudTrail log files.
     *
     * @param exception a {@link com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException}
     */
    public void handleException(ProcessingLibraryException exception) ;
}
