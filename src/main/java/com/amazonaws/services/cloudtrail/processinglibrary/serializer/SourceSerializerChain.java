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
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.CollectionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * {@link SourceSerializer} implementation that chains together multiple source serializers. When a caller passes {@link Message}
 * to this serializer, it calls all the serializers in the chain, in the original order specified, until one can parse
 * and return  {@link CloudTrailSource}. If all source serializers in the chain are called, and they cannot successfully
 * parse the message, then this class throws an {@link IOException} that indicates that no sources are available.

 * <p>
 *     This class remembers the first source serializer in the chain that can successfully parse messages, and will
 *     continue to use that serializer when there are future messages.
 * </p>
 */
public class SourceSerializerChain implements SourceSerializer {

    private final List<SourceSerializer> sourceSerializers;

    private SourceSerializer lastUsedSourceSerializer;


    /**
     * Constructs a new <code>SourceSerializerChain</code> with the specified source serializers.
     * <p>
     * Use {@link SourceSerializerFactory#createSourceSerializerChain()} for default construction.
     * </p>
     * <p>
     * When source are required from this serializer, it will call each of these source serializers in the same order
     * specified here until one of them return {@link CloudTrailSource}.
     * </p>
     *
     * @param sourceSerializers A list of at least one {@link SourceSerializer} implementation instance.
     */
    public SourceSerializerChain(List<? extends SourceSerializer> sourceSerializers) {
        if (CollectionUtils.isNullOrEmpty(sourceSerializers) || sourceSerializers.contains(null)) {
            throw new IllegalArgumentException("No source serializer specified or contains null serializers.");
        }
        this.sourceSerializers = new LinkedList<>(sourceSerializers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudTrailSource getSource(Message sqsMessage) throws IOException {
        ExceptionChain exceptionChain = new ExceptionChain();

        if (lastUsedSourceSerializer != null) {
            CloudTrailSource source = getCloudTrailSource(sqsMessage, lastUsedSourceSerializer, exceptionChain);
            if (source != null) {
                return source;
            }
        }

        for (SourceSerializer serializer: sourceSerializers) {
            // already tried lastUsedSourceSerializer, so skip it and move on
            if (serializer == lastUsedSourceSerializer) {
                continue;
            }
            CloudTrailSource source = getCloudTrailSource(sqsMessage, serializer, exceptionChain);
            if (source != null) {
                lastUsedSourceSerializer = serializer;
                return source;
            }
        }

        throw exceptionChain.throwOut();
    }

    private CloudTrailSource getCloudTrailSource(Message sqsMessage, SourceSerializer serializer, ExceptionChain exceptionChain) throws IOException {
        try {
            return serializer.getSource(sqsMessage);
        } catch (Exception e) {
            exceptionChain.addSuppressedException(e);
        }
        return null;
    }

    /**
     * The exception will provide information why every chained serializer failed to parse the message.
     */
    private class ExceptionChain {
        IOException exception;

        ExceptionChain() {
            exception =  new IOException("Unable to parse the message from any source serializers in the chain.");
        }

        void addSuppressedException(Exception e) {
            exception.addSuppressed(e);
        }

        IOException throwOut() {
            return exception;
        }
    }
}
