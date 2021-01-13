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

package sample;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;

public class SampleEventsProcessor implements EventsProcessor {
    private static final Log logger = LogFactory.getLog(SampleEventsProcessor.class);

    public void process(List<CloudTrailEvent> events) {
        int i = 0;
        for (CloudTrailEvent event : events) {
            validateEvent(event);
            logger.info(String.format("Process event %d : %s", i++, event.getEventData()));
        }
    }

    /**
     * Do simple validation before processing.
     *
     * @param event to validate
     */
    private void validateEvent(CloudTrailEvent event) {
        if (event.getEventData().getAccountId() == null) {
            logger.error(String.format("Event %s doesn't have account ID.", event.getEventData()));
        }

        // more validation here...
    }
}
