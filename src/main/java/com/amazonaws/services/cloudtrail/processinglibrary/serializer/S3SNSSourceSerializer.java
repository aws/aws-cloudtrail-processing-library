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
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SNSMessageBodyExtractor;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * The <code>S3SnsSourceSerializer</code> extracts CloudTrail log file information from notifications that Amazon S3 sends
 * to an SNS topic. Use {@link SourceSerializerFactory#createS3SNSSourceSerializer()} for initialization.
 */
public class S3SNSSourceSerializer implements SourceSerializer{
    private SNSMessageBodyExtractor messageExtractor;
    private S3SourceSerializer s3Serializer;

    public S3SNSSourceSerializer(SNSMessageBodyExtractor messageExtractor, S3SourceSerializer s3Serializer) {
        this.messageExtractor = messageExtractor;
        this.s3Serializer = s3Serializer;
    }

    @Override
    public CloudTrailSource getSource(Message sqsMessage) throws IOException {
        JsonNode s3MessageNode = messageExtractor.getMessageBody(sqsMessage);
        return s3Serializer.getCloudTrailSource(sqsMessage, s3MessageNode);
    }
}
