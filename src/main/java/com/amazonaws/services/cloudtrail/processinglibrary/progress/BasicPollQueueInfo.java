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
 * Provides basic Amazon SQS queue polling messages information.
 */
public class BasicPollQueueInfo implements ProgressInfo{
    private boolean isSuccess;
    private int polledMessageCount;

    public BasicPollQueueInfo(int polledMessageCount, boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
        this.polledMessageCount = polledMessageCount;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * @return The number of messages that are polled successfully.
     */
    public int getSuccessPolledMessageCount() {
        return polledMessageCount;
    }

    @Override
    public String toString() {
        return String.format("{isSuccess: %s, polledMessageCount: %s}", isSuccess, polledMessageCount);
    }

}
