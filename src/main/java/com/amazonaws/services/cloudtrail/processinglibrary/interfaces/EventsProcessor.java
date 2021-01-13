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

package com.amazonaws.services.cloudtrail.processinglibrary.interfaces;

import com.amazonaws.services.cloudtrail.processinglibrary.AWSCloudTrailProcessingExecutor;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;

import java.util.List;

/**
 * Provides a callback method that is used by an {@link AWSCloudTrailProcessingExecutor} to deliver AWS CloudTrail
 * records for processing.
 * <p>
 * The {@link #process(List)} is invoked after the optional {@link EventFilter}'s callback is invoked. If the
 * event was rejected by the {@link EventFilter}, then it will not be sent to {@link #process(List)}.
 * <p>
 * The number of events in the list is configurable through the <code>maxEventsPerEmit</code> property.
 *
 * @see ProcessingConfiguration
 */
public interface EventsProcessor {
    /**
     * A callback method that processes a list of {@link CloudTrailEvent}.
     * <p>
     * This callback is called by an {@link AWSCloudTrailProcessingExecutor} when it has records to process.
     *
     * @param events a list of {@link CloudTrailEvent}.
     * @throws CallbackException if an error occurs while processing <code>events</code>.
     */
    public void process(List<CloudTrailEvent> events) throws CallbackException;
}
