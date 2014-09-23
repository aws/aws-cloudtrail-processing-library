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
package com.amazonaws.services.cloudtrail.clientlibrary.progress;


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
        return this.isSuccess;
    }

    /**
     * @return the successPolledMessageCount
     */
    public int getSuccessPolledMessageCount() {
        return this.polledMessageCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{isSuccess: ");
        builder.append(isSuccess);
        builder.append(", polledMessageCount: ");
        builder.append(polledMessageCount);
        builder.append("}");
        return builder.toString();
    }

}