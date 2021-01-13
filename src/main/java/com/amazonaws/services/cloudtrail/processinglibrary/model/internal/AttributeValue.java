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

/**
 * POJO (Plain Old Java Object) that represents the attribute value entitle for a specific attribute type.
 * Contains the value of the attribute and average for a specified time range
 */
public class AttributeValue extends CloudTrailDataStore {

    /**
     * Get the value of the attribute.
     *
     * @return a string value representation of the attribute.
     */
    public String getValue() {
        return (String) this.get(CloudTrailEventField.value.name());
    }

    /**
     * Get the average number of occurrences for the attribute value within a time range (either the time range of
     * insightDuration or baselineDuration).
     *
     * @return {@link Double} representation, which is precise to the 10th decimal number.
     */
    public Double getAverage() {
        return (Double) this.get(CloudTrailEventField.average.name());
    }
}
