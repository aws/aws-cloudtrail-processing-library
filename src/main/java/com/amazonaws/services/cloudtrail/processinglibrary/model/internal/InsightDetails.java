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
 * Shows information about the underlying triggers of an Insights event, such as event source,
 * statistics, API name, and whether the event is the start or end of the Insights event.
 */
public class InsightDetails extends CloudTrailDataStore {
    /**
     * Get event name
     *
     * @return The AWS API for which unusual activity was detected.
     */
    public String getEventName() {
        return (String) this.get(CloudTrailEventField.eventName.name());
    }


    /**
     * Get event source
     *
     * @return The service that the request was made to. This name is typically a short form of the service name without spaces plus .amazonaws.com.
     */
    public String getEventSource() {
        return (String) this.get(CloudTrailEventField.eventSource.name());
    }


    /**
     * Get insight context
     *
     * @return {@link InsightContext} Data about the rate of calls that triggered the Insights event compared to the normal rate of calls to the subject API per minute.
     */
    public InsightContext getInsightContext() {
        return (InsightContext) this.get(CloudTrailEventField.insightContext.name());
    }


    /**
     * Get insight type
     *
     * @return The type of Insights event. Value is ApiCallRateInsight.
     */
    public String getInsightType() {
        return (String) this.get(CloudTrailEventField.insightType.name());
    }


    /**
     * Get state
     *
     * @return Shows whether the event represents the start or end of the insight (the start or end of unusual activity). Values are start or end.
     */
    public String getState() {
        return (String) this.get(CloudTrailEventField.state.name());
    }


}
