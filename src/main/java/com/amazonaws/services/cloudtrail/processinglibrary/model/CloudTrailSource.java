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

package com.amazonaws.services.cloudtrail.processinglibrary.model;

import java.util.Map;

/**
 * A skeleton class used for source filter.
 * <p>
 * When using it, users should cast it to a specific subclass.
 */
public interface CloudTrailSource {
    /**
     * Retrieve {@link CloudTrailSource} attributes.
     *
     * @return a Map containing key/value pairs representing the attributes of the CloudTrailSource.
     */
    public Map<String, String> getSourceAttributes();
}
