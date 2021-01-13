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

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.LogDeliveryInfo;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * The implementation of raw CloudTrail log's event serializer.
 */
public class RawLogDeliveryEventSerializer extends AbstractEventSerializer{
    private String logFile;
    private CloudTrailLog ctLog;

    public RawLogDeliveryEventSerializer(String logFile, CloudTrailLog ctLog, JsonParser jsonParser) throws IOException {
        super(jsonParser);
        this.ctLog = ctLog;
        this.logFile = logFile;
        readArrayHeader();
    }

    /**
     * Find the raw event in string format from logFileContent based on character start index and end index.
     */
    @Override
    public CloudTrailEventMetadata getMetadata(int charStart, int charEnd) {
        // Use Jackson getTokenLocation API only return the , (Comma) position, we need to advance to first open curly brace.
        String rawEvent = logFile.substring(charStart, charEnd+1);
        int offset = rawEvent.indexOf("{");
        rawEvent = rawEvent.substring(offset);
        CloudTrailEventMetadata metadata = new LogDeliveryInfo(ctLog, charStart + offset, charEnd, rawEvent);
        return metadata;
    }
}
