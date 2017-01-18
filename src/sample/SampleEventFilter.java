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

package sample;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventData;

public class SampleEventFilter implements EventFilter{

    private static final String EC2_EVENTS = "ec2.amazonaws.com";

    /**
     * Event filter that only keep EC2 deletion API calls.
     */
    @Override
    public boolean filterEvent(CloudTrailEvent event) throws CallbackException {
        CloudTrailEventData eventData = event.getEventData();

        String eventSource = eventData.getEventSource();
        String eventName = eventData.getEventName();

        return eventSource.equals(EC2_EVENTS) && eventName.startsWith("Delete");
    }
}
