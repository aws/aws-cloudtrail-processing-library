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

package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import java.util.Map;

/**
 * Information about the Edge device details of a service API call.
 */
public class EdgeDeviceDetails extends CloudTrailDataStore{
    public String getType() {
        return (String) this.get(CloudTrailEventField.type.name());
    }

    public String getSnowJobId() {
        return (String) this.get(CloudTrailEventField.snowJobId.name());
    }

    public String getDeviceId() {
        return (String) this.get(CloudTrailEventField.deviceId.name());
    }

    public String getDeviceFamily() {
        return (String) this.get(CloudTrailEventField.deviceFamily.name());
    }

    /**
     * Get attributes
     *
     * @return additional edge device attributes
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, String> getAttributes() {
        return (Map) this.get(CloudTrailEventField.attributes.name());
    }
}
