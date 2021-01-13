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
 * A container for data about the typical average rate of calls to the subject API by an account,
 * the rate of calls that triggered the Insights event, and the duration, in minutes, of the Insights event.
 */
public class InsightStatistics extends CloudTrailDataStore {

    /**
     * Get baseline
     *
     * @return Shows the typical average rate of calls to the subject API by an account.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Double> getBaseline() {
        return (Map<String, Double>) this.get(CloudTrailEventField.baseline.name());
    }

    /**
     * Get insight
     *
     * @return Shows the unusual rate of calls to the subject API that triggers the logging of an Insights event.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Double> getInsight() {
        return (Map<String, Double>) this.get(CloudTrailEventField.insight.name());
    }

    /**
     * Get insight duration
     *
     * @return The duration, in minutes, of an Insights event (the time period from the start to end of unusual activity on the subject API).
     */
    public Integer getInsightDuration() {
        return (Integer) this.get(CloudTrailEventField.insightDuration.name());
    }

    /**
     * Get baseline duration
     *
     * @return The baseline duration, in minutes, of an Insights event. Start day and time is 7 days before an Insights
     * event occurs, rounded down to a full (or integral) day. The end time is when the Insights event occurs.
     */
    public Integer getBaselineDuration() {
        return (Integer) this.get(CloudTrailEventField.baselineDuration.name());
    }

}
