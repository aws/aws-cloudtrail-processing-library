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

package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import java.util.Map;

/**
 * If the request was made with temporary security credentials obtained using web
 * identity federation, an element that lists information about the identity provider
 */
public class WebIdentitySessionContext extends CloudTrailDataStore{
    /**
     * Get federated provider
     * @return Who To grant temporary access to a non-AWS user
     */
    public String getFederatedProvider() {
        return (String) this.get(CloudTrailEventField.federatedProvider.name());
    }

    /**
     * Get attributes
     *
     * @return additional web identity session contest attributes
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, String> getAttributes() {
        return (Map) this.get(CloudTrailEventField.attributes.name());
    }
}