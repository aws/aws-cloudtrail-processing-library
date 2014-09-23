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

import com.amazonaws.services.cloudtrail.clientlibrary.exceptions.ClientLibraryException;

/**
 * ExceptionHandler is a call back function that handles exception cases during processing
 * CloudTrail log files.
 *
 * The handleException() method is invoked in 6 different cases:
 *
 * 1. Error happened at polling messages from SQS
 * 2. Error happened at parsing message from SQS
 * 3. Error happened at deleting messages from SQS
 * 4. Error happened at downloading CloudTrail log file
 * 5. Error happened at parsing CloudTrail log file
 * 6. Uncaught Exception happened.
 *
 * ClientLibraryException contains execution context - ProgressStatus. Depends on when the error happened,
 * PorgressStatus provides different informations.
 *
 * See {@link com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus} for more information.
 */
public interface ExceptionHandler {
    public void handleException(ClientLibraryException exception) ;
}
