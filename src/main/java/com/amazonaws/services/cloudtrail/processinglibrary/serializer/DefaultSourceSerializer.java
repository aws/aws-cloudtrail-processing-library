/*******************************************************************************
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SourceAttributeKeys;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultSourceSerializer implements SourceSerializer {

    private static final String S3_OBJECT_KEY = "s3ObjectKey";
    private static final String S3_BUCKET = "s3Bucket";
    private static final String MESSAGE = "Message";

    /**
     * An instance of ObjectMapper
     */
    private ObjectMapper mapper;

    public DefaultSourceSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * This method may modify original sqsMessage's attributes.
     */
    @Override
    public CloudTrailSource getSource(Message sqsMessage) throws IOException {
        List<CloudTrailLog> cloudTrailLogs = new ArrayList<>();
        List<String> objectKeys = new ArrayList<String>();
        String bucketName = null;
        String accountId = null;

        String messageText = this.mapper.readTree(sqsMessage.getBody()).get(MESSAGE).textValue();
        JsonNode messageNode = this.mapper.readTree(messageText);

        // Parse arbitrary S3 notifications (not only Cloudtrail-specific ones)
        JsonNode records = messageNode.get("Records");
        if (records != null && records.isArray()) {
            for (JsonNode record : records) {
                try {
                    bucketName = JsonPath.read(record.toString(), "$.s3.bucket.name");
                    objectKeys.add(JsonPath.read(record.toString(), "$.s3.object.key").toString());
                } catch (PathNotFoundException ignored) {
                }
            }
        } else {
            // parse message body
            Iterator<String> it = messageNode.fieldNames();
            while (it.hasNext()) {
                String key = it.next();
                String value = messageNode.path(key).textValue();
                if (S3_BUCKET.equals(key)) {
                    bucketName = value;
                } else if (S3_OBJECT_KEY.equals(key)) {
                    objectKeys = this.mapper.readValue(messageNode.get(S3_OBJECT_KEY).traverse(), new TypeReference<List<String>>() {

                    });
                } else {
                    // rest of attributes from message body will be added to SQS message's attributes.
                    sqsMessage.addAttributesEntry(key, value);
                }
            }
        }

        for (String objectKey : objectKeys) {
            accountId = accountId == null ? LibraryUtils.extractAccountIdFromObjectKey(objectKey.toString()) : accountId;
            cloudTrailLogs.add(new CloudTrailLog(bucketName, objectKey));
        }

        //set accountId as message attribute
        sqsMessage.addAttributesEntry(SourceAttributeKeys.ACCOUNT_ID.getAttributeKey(), accountId);

        return new SQSBasedSource(sqsMessage, cloudTrailLogs);
    }
}
