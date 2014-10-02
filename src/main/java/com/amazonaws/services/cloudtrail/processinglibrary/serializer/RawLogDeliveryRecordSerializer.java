/*******************************************************************************
 * Copyright (c) 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import java.io.IOException;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailDeliveryInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.LogDeliveryInfo;
import com.fasterxml.jackson.core.JsonParser;

public class RawLogDeliveryRecordSerializer extends AbstractRecordSerializer{
    private String logFile;
    private CloudTrailLog ctLog;

    public RawLogDeliveryRecordSerializer(String logFile, CloudTrailLog ctLog, JsonParser jsonParser) throws IOException {
        super(jsonParser);
        this.ctLog = ctLog;
        this.logFile = logFile;
        this.readArrayHeader();
    }

    /**
     * Find the raw record in string format from logFileContent based on character start index and end index
     */
    @Override
    public CloudTrailDeliveryInfo getDeliveryInfo(int charStart, int charEnd) {
        // Use Jackson getTokenLocation API only return the , (Comma) position, we need to advance to first open curly brace.
        String rawRecord = logFile.substring(charStart, charEnd+1);
        int offset = rawRecord.indexOf("{");
        rawRecord = rawRecord.substring(offset);
        CloudTrailDeliveryInfo deliveryInfo = new LogDeliveryInfo(ctLog, charStart + offset, charEnd, rawRecord);
        return deliveryInfo;
    }
}
