/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.IOException;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.LogDeliveryInfo;
import com.fasterxml.jackson.core.JsonParser;

public class DefaultEventSerializer extends AbstractEventSerializer{
    private CloudTrailLog ctLog;

    /**
     * Default implementation of {@link EventSerializer}
     *
     * @param ctLog
     * @param jsonParser
     * @throws IOException
     */
    public DefaultEventSerializer(CloudTrailLog ctLog, JsonParser jsonParser) throws IOException {
        super(jsonParser);
        this.ctLog = ctLog;
        this.readArrayHeader();
    }

    @Override
    public CloudTrailEventMetadata getMetadata(int charStart, int charEnd) {
        CloudTrailEventMetadata deliveryInfo = new LogDeliveryInfo(ctLog, -1, -1, null);
        return deliveryInfo;
    }
}
