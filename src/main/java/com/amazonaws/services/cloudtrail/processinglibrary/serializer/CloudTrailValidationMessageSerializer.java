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

package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import com.amazonaws.services.cloudtrail.processinglibrary.factory.SourceSerializerFactory;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SourceAttributeKeys;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.SourceType;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SNSMessageBodyExtractor;
import com.amazonaws.services.sqs.model.Message;

import java.io.IOException;

/**
 * The <code>CloudTrailValidationMessageSerializer</code> extracts CloudTrail validation message from notifications that CloudTrail
 * sends to an SNS topic. Use {@link SourceSerializerFactory#createCloudTrailValidationMessageSerializer()} for default initialization.
 */
public class CloudTrailValidationMessageSerializer implements SourceSerializer {
    private static final String CLOUD_TRAIL_VALIDATION_MESSAGE = "CloudTrail validation message.";

    private SNSMessageBodyExtractor messageExtractor;

    public CloudTrailValidationMessageSerializer(SNSMessageBodyExtractor messageExtractor) {
        this.messageExtractor = messageExtractor;
    }

    @Override
    public CloudTrailSource getSource(Message sqsMessage) throws IOException {
        if (messageExtractor.getMessageText(sqsMessage).equals(CLOUD_TRAIL_VALIDATION_MESSAGE)) {
            sqsMessage.addAttributesEntry(SourceAttributeKeys.SOURCE_TYPE.getAttributeKey(), SourceType.CloudTrailValidationMessage.name());
            return new SQSBasedSource(sqsMessage, null);
        }

        return null;
    }
}
