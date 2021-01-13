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

package com.amazonaws.services.cloudtrail.processinglibrary.exceptions;

import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;

/**
 * Exceptions of this type are handled by an implementation of the {@link ExceptionHandler} interface.
 * <p>
 * The status of the operation that was in progress when the exception occurred can be retrieved by calling
 * the {@link #getStatus()}.
 * </p>
 */
public class ProcessingLibraryException extends Exception {
    private static final long serialVersionUID = 8757412348402829171L;

    /**
     * The {@link ProgressStatus} of the operation that was in progress when the exception occurred.
     */
    private ProgressStatus status;

    /**
     * Initializes a new <code>ProcessingLibraryException</code> with a message and status.
     *
     * @param message A string that provides information about the exception.
     * @param status The {@link ProgressStatus} of the operation that was in progress when the exception occurred.
     */
    public ProcessingLibraryException(String message, ProgressStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Initializes a new <code>ProcessingLibraryException</code> with a message, inner exception, and status.
     *
     * @param message A string that provides information about the exception.
     * @param e An inner exception that is carried along with this exception.
     * @param status The {@link ProgressStatus} of the operation that was in progress when the exception occurred.
     */
    public ProcessingLibraryException(String message, Exception e, ProgressStatus status) {
        super(message, e);
        this.status = status;
    }

    /**
     * Get the status of the operation that was in progress when the exception occurred.
     *
     * @return A {@link ProgressStatus} object that provides information about when the exception occurred.
     */
    public ProgressStatus getStatus() {
        return status;
    }
}
