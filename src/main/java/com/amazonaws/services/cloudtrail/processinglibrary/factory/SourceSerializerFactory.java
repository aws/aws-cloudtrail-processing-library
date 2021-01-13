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

package com.amazonaws.services.cloudtrail.processinglibrary.factory;

import com.amazonaws.services.cloudtrail.processinglibrary.serializer.*;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SNSMessageBodyExtractor;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.SourceIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Factory for creating {@link SourceSerializer}.
 */
public class SourceSerializerFactory {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SNSMessageBodyExtractor snsMessageExtractor = new SNSMessageBodyExtractor(mapper);
    private static final SourceIdentifier sourceIdentifier = new SourceIdentifier();
    /**
     * Default {@link SourceSerializerChain} construction.
     * <p>
     *     This is the default serializer which is used if you do not provide a serializer.
     * </p>
     *
     * @return {@link SourceSerializer} Each of these source serializers is in the specified order.
     */
    public static SourceSerializer createSourceSerializerChain() {
        List<SourceSerializer> sourceSerializers = new ArrayList<>(Arrays.asList(
                createCloudTrailSourceSerializer(),
                createS3SNSSourceSerializer(),
                createS3SourceSerializer(),
                createCloudTrailValidationMessageSerializer()
                ));
        return new SourceSerializerChain(sourceSerializers);
    }

    /**
     * Default {@link CloudTrailSourceSerializer} construction.
     * @return {@link SourceSerializer}.
     */
    public static CloudTrailSourceSerializer createCloudTrailSourceSerializer() {
        return new CloudTrailSourceSerializer(snsMessageExtractor, mapper, sourceIdentifier);
    }

    /**
     * Default {@link S3SourceSerializer} construction.
     * @return {@link SourceSerializer}.
     */
    public static S3SourceSerializer createS3SourceSerializer() {
        return new S3SourceSerializer(mapper, sourceIdentifier);
    }

    /**
     * Default {@link S3SNSSourceSerializer} construction.
     * @return {@link SourceSerializer}.
     */
    public static S3SNSSourceSerializer createS3SNSSourceSerializer() {
        return new S3SNSSourceSerializer(snsMessageExtractor, createS3SourceSerializer());
    }

    /**
     * Default {@link CloudTrailValidationMessageSerializer} construction.
     * @return {@link SourceSerializer}.
     */
    public static CloudTrailValidationMessageSerializer createCloudTrailValidationMessageSerializer() {
        return new CloudTrailValidationMessageSerializer(snsMessageExtractor);
    }
}
