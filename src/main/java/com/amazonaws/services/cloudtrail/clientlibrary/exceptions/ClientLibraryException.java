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
 * This exception is subject to be handled by implementation of ExceptionHandler.
 * Based on different scenario that error happened different ProgressStatus will be handed to ExceptionHandler.
 */
public class ClientLibraryException extends Exception {
    private static final long serialVersionUID = 8757412348402829171L;

    /**
     * Progress status when exception happened.
     */
    private ProgressStatus status;

    public ClientLibraryException(String message, ProgressStatus status) {
        super(message);
    }

    public ClientLibraryException(String message, Exception e, ProgressStatus status) {
        super(message, e);
    }

    /**
     * @return the status when exception happened.
     */
    public ProgressStatus getStatus() {
        return status;
    }
}