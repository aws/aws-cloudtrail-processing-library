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
package com.amazonaws.services.cloudtrail.clientlibrary.exceptions;

import com.amazonaws.services.cloudtrail.clientlibrary.progress.ProgressStatus;

/**
 * The exception from call back to implementation of AWSCloudTrailClientLibrary interfaces.
 */
public class CallbackException extends ClientLibraryException{

    private static final long serialVersionUID = -2425808722370565843L;

    public CallbackException(String message, ProgressStatus status) {
        super(message, status);
    }


    public CallbackException(String message, Exception e, ProgressStatus status) {
        super(message, e, status);
    }
}
