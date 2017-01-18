/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

public class BasicParseMessageInfo implements ProgressMessageInfo {
    private boolean isSuccess;
    private Message message;

    /**
     * Provide basic message parsing information
     *
     * @param message the SQS message
     * @param isSuccess whether successfully parsed SQS message
     */
    public BasicParseMessageInfo(Message message, boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
        this.message = message;
    }

    @Override
    public Message getMessage() {
        return this.message;
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }
}
