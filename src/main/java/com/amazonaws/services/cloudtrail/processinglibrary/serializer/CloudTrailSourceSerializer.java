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
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SourceAttributeKeys;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.SourceType;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SNSMessageBodyExtractor;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SourceIdentifier;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>CloudTrailSourceSerializer</code> extracts CloudTrail log file information from notifications that CloudTrail
 * sends to an SNS topic. Use {@link SourceSerializerFactory#createCloudTrailSourceSerializer()} for default initialization.
 */
public class CloudTrailSourceSerializer implements SourceSerializer {

    private static final String S3_BUCKET_NAME = "s3Bucket";
    private static final String S3_OBJECT_KEY = "s3ObjectKey";

    private SNSMessageBodyExtractor messageExtractor;
    private ObjectMapper mapper;
    private SourceIdentifier sourceIdentifier;

    public CloudTrailSourceSerializer(SNSMessageBodyExtractor messageExtractor, ObjectMapper mapper, SourceIdentifier sourceIdentifier) {
        this.messageExtractor = messageExtractor;
        this.mapper = mapper;
        this.sourceIdentifier = sourceIdentifier;
    }

    @Override
    public CloudTrailSource getSource(Message sqsMessage) throws IOException {
        List<CloudTrailLog> cloudTrailLogs = new ArrayList<>();
        JsonNode messageNode = messageExtractor.getMessageBody(sqsMessage);

        addCloudTrailLogsAndMessageAttributes(sqsMessage, cloudTrailLogs, messageNode);
        addRestMessageAttributes(sqsMessage, messageNode);

        return new SQSBasedSource(sqsMessage, cloudTrailLogs);
    }

    /**
     * As long as there is at least one CloudTrail log object:
     * <p>
     *     <li>Add the CloudTrail log object key to the list.</li>
     *     <li>Add <code>accountId</code> extracted from log object key to <code>sqsMessage</code>.</li>
     *     <li>Add {@link SourceType#CloudTrailLog} to the <code>sqsMessage</code>.</li>
     * </p>
     *
     * If there is no CloudTrail log object and it is a valid CloudTrail message, CPL adds only {@link SourceType#Other}
     * to the <code>sqsMessage</code>.
     *
     */
    private void addCloudTrailLogsAndMessageAttributes(Message sqsMessage, List<CloudTrailLog> cloudTrailLogs, JsonNode messageNode) throws IOException {
        SourceType sourceType = SourceType.Other;

        String bucketName = messageNode.get(S3_BUCKET_NAME).textValue();
        List<String> objectKeys = mapper.readValue(messageNode.get(S3_OBJECT_KEY).traverse(), new TypeReference<List<String>>() {});

        for (String objectKey: objectKeys) {
            SourceType currSourceType = sourceIdentifier.identify(objectKey);
            if (currSourceType == SourceType.CloudTrailLog) {
                cloudTrailLogs.add(new CloudTrailLog(bucketName, objectKey));
                sourceType = currSourceType;
                LibraryUtils.setMessageAccountId(sqsMessage, objectKey);
            }
        }

        sqsMessage.addAttributesEntry(SourceAttributeKeys.SOURCE_TYPE.getAttributeKey(), sourceType.name());
    }

    /**
     * Excluding S3_BUCKET, S3_OBJECT_KEY, add all other attributes from the message body to <code>sqsMessage</code>.
     * @param sqsMessage The SQS message.
     * @param messageNode The message body.
     */
    private void addRestMessageAttributes(Message sqsMessage, JsonNode messageNode) {
        Iterator<String> it = messageNode.fieldNames();
        while(it.hasNext()) {
            String key = it.next();
            if (!key.equals(S3_OBJECT_KEY) && !key.equals(S3_BUCKET_NAME)) {
                sqsMessage.addAttributesEntry(key, messageNode.get(key).textValue());
            }
        }
    }


}
