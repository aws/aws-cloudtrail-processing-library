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

import java.util.List;

/**
 * Data structure that contains attribute value entities for a specific attribute type (for example, userIdentityArn,
 * userAgent and errorCode), available for both insightDuration and baselineDuration of an Insights event.
 */
public class InsightAttributions extends CloudTrailDataStore {

    /**
     * Get attribute type
     *
     * @return type, or name of this {@link InsightAttributions}. For example, "userIdentityArn".
     */
    public String getAttribute() {
        return (String) this.get(CloudTrailEventField.attribute.name());
    }

    /**
     * Get list of attribute values for the duration of the Insights event.
     *
     * @return list of {@link AttributeValue}, sorted by their average numbers in descending order.
     */
    @SuppressWarnings("unchecked")
    public List<AttributeValue> getInsight() {
        return (List<AttributeValue>) this.get(CloudTrailEventField.insight.name());
    }

    /**
     * Get the list of attribute values for the baselineDuration of the Insights event, which is about the seven-day
     * period before the start time of the Insights event.
     *
     * @return list of {@link AttributeValue}, sorted by their average numbers in descending order.
     */
    @SuppressWarnings("unchecked")
    public List<AttributeValue> getBaseline() {
        return (List<AttributeValue>) this.get(CloudTrailEventField.baseline.name());
    }
}
