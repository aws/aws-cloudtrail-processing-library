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
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SourceIdentifier;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>S3SourceSerializer</code> extracts CloudTrail log file information from notifications sent directly
 * by Amazon S3. Use {@link SourceSerializerFactory#createS3SourceSerializer()} for initialization.
 */
public class S3SourceSerializer implements SourceSerializer {
    private static final JsonPointer S3_BUCKET_NAME = JsonPointer.compile("/s3/bucket/name");
    private static final JsonPointer S3_OBJECT_KEY = JsonPointer.compile("/s3/object/key");
    private static final String RECORDS = "Records";
    private static final String EVENT_NAME = "eventName";

    private ObjectMapper mapper;
    private SourceIdentifier sourceIdentifier;

    public S3SourceSerializer(ObjectMapper mapper, SourceIdentifier sourceIdentifier) {
        this.mapper = mapper;
        this.sourceIdentifier = sourceIdentifier;
    }

    @Override
    public CloudTrailSource getSource(Message sqsMessage) throws IOException{
        JsonNode s3MessageNode = mapper.readTree(sqsMessage.getBody());
        return getCloudTrailSource(sqsMessage, s3MessageNode);
    }

    public CloudTrailSource getCloudTrailSource(Message sqsMessage, JsonNode s3MessageNode) throws IOException {
        JsonNode s3RecordsNode = s3MessageNode.get(RECORDS);
        List<CloudTrailLog> cloudTrailLogs = new ArrayList<>();

        addCloudTrailLogsAndMessageAttributes(sqsMessage, s3RecordsNode, cloudTrailLogs);

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
     * If there is no CloudTrail log object and it is a valid S3 message, CPL adds only {@link SourceType#Other}
     * to the <code>sqsMessage</code>.
     *
     */
    private void addCloudTrailLogsAndMessageAttributes(Message sqsMessage, JsonNode s3RecordsNode, List<CloudTrailLog> cloudTrailLogs) {
        SourceType sourceType = SourceType.Other;

        for (JsonNode s3Record: s3RecordsNode) {
            String bucketName = s3Record.at(S3_BUCKET_NAME).textValue();
            String objectKey = s3Record.at(S3_OBJECT_KEY).textValue();
            String eventName = s3Record.get(EVENT_NAME).textValue();

            SourceType currSourceType = sourceIdentifier.identifyWithEventName(objectKey, eventName);
            if (currSourceType == SourceType.CloudTrailLog) {
                cloudTrailLogs.add(new CloudTrailLog(bucketName, objectKey));
                sourceType = currSourceType;
                LibraryUtils.setMessageAccountId(sqsMessage, objectKey);
            }
        }

        sqsMessage.addAttributesEntry(SourceAttributeKeys.SOURCE_TYPE.getAttributeKey(), sourceType.name());
    }
}
