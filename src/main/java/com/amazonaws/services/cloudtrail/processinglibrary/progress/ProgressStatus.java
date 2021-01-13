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

package com.amazonaws.services.cloudtrail.processinglibrary.progress;

/**
 * Provides contextual information about the state of an AWS CloudTrail
 * Processing Library operation.
 */
public class ProgressStatus {

    private ProgressState progressState;
    private ProgressInfo progressInfo;

    /**
     * Initializes a new <code>ProgressStatus</code> object.
     *
     * @param progressState The {@link ProgressState}.
     * @param progressInfo The {@link ProgressInfo}.
     */
    public ProgressStatus(ProgressState progressState, ProgressInfo progressInfo) {
        this.progressState = progressState;
        this.progressInfo = progressInfo;
    }

    /**
     * @return The state of the progress.
     */
    public ProgressState getProgressState() {
        return progressState;
    }

    /**
     * Sets the <code>ProgressState</code> of this object.
     */
    public void setProgressState(ProgressState progressState) {
        this.progressState = progressState;
    }

    /**
     * @return The basic progress information.
     */
    public ProgressInfo getProgressInfo() {
        return progressInfo;
    }

    /**
     * Sets the <code>ProgressInfo</code> for this object.
     *
     * @param progressInfo The progressInfo to set.
     */
    public void setProgressInfo(ProgressInfo progressInfo) {
        this.progressInfo = progressInfo;
    }

    /**
     * Creates a string representation of this object.
     *
     * @return A string containing the values of {@link ProgressState} and {@link ProgressInfo}.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (progressState != null)
            builder.append("progressState: ").append(progressState).append(", ");
        if (progressInfo != null)
            builder.append("progressInfo: ").append(progressInfo);
        builder.append("}");
        return builder.toString();
    }


    /**
     * Calculates a hash code for the current state of this {@link ProgressStatus}.
     * <p>
     * The hash code will change if the values of {@link ProgressState} and {@link ProgressInfo} change.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((progressState == null) ? 0 : progressState.hashCode());
        result = prime * result + ((progressInfo == null) ? 0 : progressInfo.hashCode());
        return result;
    }

    /**
     * Compares this object with another {@link ProgressStatus} object.
     *
     * @return <code>true</code> if the objects are equal. Otherwise, <code>false</code>.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProgressStatus other = (ProgressStatus) obj;
        if (progressState != other.progressState)
            return false;
        if (progressInfo == null) {
            if (other.progressInfo != null)
                return false;
        } else if (!progressInfo.equals(other.progressInfo))
            return false;
        return true;
    }

}
