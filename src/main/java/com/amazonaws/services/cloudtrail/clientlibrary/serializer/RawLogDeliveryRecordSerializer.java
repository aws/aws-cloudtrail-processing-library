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
package com.amazonaws.services.cloudtrail.clientlibrary.serializer;

import java.io.IOException;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailDeliveryInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.model.LogDeliveryInfo;
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
        CloudTrailDeliveryInfo deliveryInfo = new LogDeliveryInfo(ctLog, charStart, charEnd, logFile.substring(charStart, charEnd + 1));
        return deliveryInfo;
    }
}
