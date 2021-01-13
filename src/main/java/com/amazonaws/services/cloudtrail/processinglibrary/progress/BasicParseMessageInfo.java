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

import com.amazonaws.services.sqs.model.Message;

/**
 * Provide basic message parsing information.
 */
public class BasicParseMessageInfo implements ProgressMessageInfo {
    private boolean isSuccess;
    private Message message;


    public BasicParseMessageInfo(Message message, boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
        this.message = message;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{isSuccess: ");
        builder.append(isSuccess);
        builder.append(", MessageToParse: ");
        builder.append(message.toString());
        builder.append("}");

        return builder.toString();
    }

    @Override
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
