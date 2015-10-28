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

package com.amazonaws.services.cloudtrail.processinglibrary.interfaces;

import java.util.List;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;

/**
 * Provides a callback method that is used by an
 * {@link com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor} to deliver AWS CloudTrail
 * records for processing.
 * <p>
 * The <code>process()</code> method is invoked after the optional {@link EventFilter}'s callback is invoked. If the
 * event was rejected by the <code>EventFilter</code>, then it will not be sent to <code>process()</code>.
 * <p>
 * The number of events in the list is configurable through the <code>maxEventsPerEmit</code> property.
 *
 * @see com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguraton
 */
public interface EventsProcessor {
    /**
     * A callback method that processes a list of <code>CloudTrailEvent</code> events.
     * <p>
     * This callback is called by an
     * {@link com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor}
     * when it has records to process.
     *
     * @param events a list of {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent}
     *     objects.
     * @throws CallbackException if an error occurs while processing events.
     */
    public void process(List<CloudTrailEvent> events) throws CallbackException;
}
