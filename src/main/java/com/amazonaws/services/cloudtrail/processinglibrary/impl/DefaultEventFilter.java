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

package com.amazonaws.services.cloudtrail.processinglibrary.impl;

import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;

/**
 * Default implementation of EventFilter that simply return True for any CloudTrailEvent
 */
public class DefaultEventFilter implements EventFilter {

    @Override
    public boolean filterEvent(CloudTrailEvent event) {
        return true;
    }
}
