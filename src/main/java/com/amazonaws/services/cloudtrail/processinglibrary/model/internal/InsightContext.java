/*******************************************************************************
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * Data about the rate of calls that triggered the Insights event
 * compared to the normal rate of calls to the subject API per minute.
 */
public class InsightContext extends CloudTrailDataStore {

    /**
     * Get insight statistics
     *
     * @return {@link InsightStatistics}
     */
    public InsightStatistics getStatistics() {
        return (InsightStatistics) this.get(CloudTrailEventField.statistics.name());
    }

    /**
     * Get Insights event attributions. Each {@link InsightAttributions} contains attribute values for a specific
     * type of attribute, such as userIdentityArn, userAgent and errorCode.
     *
     * @return list of {@link InsightAttributions}
     */
    @SuppressWarnings("unchecked")
    public List<InsightAttributions> getAttributions() {
        return (List<InsightAttributions>) this.get(CloudTrailEventField.attributions.name());
    }

}