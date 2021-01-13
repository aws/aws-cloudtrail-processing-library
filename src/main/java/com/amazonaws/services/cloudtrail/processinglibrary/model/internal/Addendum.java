/*******************************************************************************
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * The Addendum block of an addendum event includes details to fill an auditing gap or update an older event.
 *
 * It shows details of an addendum to an older event, such as a reason for delivery,
 * updated fields, original request ID, and original event ID.
 */
public class Addendum extends CloudTrailDataStore {
    /**
     * Get reason
     *
     * @return The reason for the delivery of the addendum event.
     */
    public String getReason() {
        return (String) this.get(CloudTrailEventField.reason.name());
    }


    /**
     * Get updated fields
     *
     * @return A string of comma-delimited updated fields.
     */
    public String getUpdatedFields() {
        return (String) this.get(CloudTrailEventField.updatedFields.name());
    }


    /**
     * Get original request ID
     *
     * @return The request ID that matches the original delivered event. If there is no original delivered event, the value is null.
     */
    public String getOriginalRequestID() {
        return (String) this.get(CloudTrailEventField.originalRequestID.name());
    }


    /**
     * Get original event ID
     *
     * @return The event ID that matches the original delivered event. If there is no original delivered event, the value is null.
     */
    public String getOriginalEventID() {
        return (String) this.get(CloudTrailEventField.originalEventID.name());
    }

}
