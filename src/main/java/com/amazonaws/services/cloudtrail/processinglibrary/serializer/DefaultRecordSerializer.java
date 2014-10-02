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

public class DefaultRecordSerializer extends AbstractRecordSerializer{
    private CloudTrailLog ctLog;

    public DefaultRecordSerializer(CloudTrailLog ctLog, JsonParser jsonParser) throws IOException {
        super(jsonParser);
        this.ctLog = ctLog;
        this.readArrayHeader();
    }

    @Override
    public CloudTrailDeliveryInfo getDeliveryInfo(int charStart, int charEnd) {
        CloudTrailDeliveryInfo deliveryInfo = new LogDeliveryInfo(ctLog, -1, -1, null);
        return deliveryInfo;
    }
}
