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
package com.amazonaws.services.cloudtrail.processinglibrary.utils;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Extract message body from the SNS notification, specifically, the value of the 'Message' attribute.

 */
public class SNSMessageBodyExtractor {
    private static final String MESSAGE = "Message";
    private final ObjectMapper mapper;

    public SNSMessageBodyExtractor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode getMessageBody(Message sqsMessage) throws IOException, NullPointerException {
        return mapper.readTree(getMessageText(sqsMessage));
    }

    public String getMessageText(Message sqsMessage) throws IOException, NullPointerException {
        return mapper.readTree(sqsMessage.getBody()).get(MESSAGE).textValue();
    }
}
