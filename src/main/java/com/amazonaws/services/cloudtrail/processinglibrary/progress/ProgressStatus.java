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
package com.amazonaws.services.cloudtrail.processinglibrary.progress;

public class ProgressStatus {

    private ProgressState state;
    private ProgressInfo statusInfo;

    /**
     * Progress status
     *
     * @param state progress state
     * @param statusInfo progress information
     */
    public ProgressStatus(ProgressState state, ProgressInfo statusInfo) {
        this.state = state;
        this.statusInfo = statusInfo;
    }

    /**
     * @return the state
     */
    public ProgressState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(ProgressState state) {
        this.state = state;
    }

    /**
     * @return the statusInfo
     */
    public ProgressInfo getStatusInfo() {
        return statusInfo;
    }

    /**
     * @param statusInfo the statusInfo to set
     */
    public void setStatusInfo(ProgressInfo statusInfo) {
        this.statusInfo = statusInfo;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (state != null)
            builder.append("state: ").append(state).append(", ");
        if (statusInfo != null)
            builder.append("statusInfo: ").append(statusInfo);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result
                + ((statusInfo == null) ? 0 : statusInfo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProgressStatus other = (ProgressStatus) obj;
        if (state != other.state)
            return false;
        if (statusInfo == null) {
            if (other.statusInfo != null)
                return false;
        } else if (!statusInfo.equals(other.statusInfo))
            return false;
        return true;
    }

}
