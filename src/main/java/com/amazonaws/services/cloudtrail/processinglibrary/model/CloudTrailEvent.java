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

package com.amazonaws.services.cloudtrail.processinglibrary.model;

/**
 * Provides AWS CLoudTrail log information to your
 * {@link com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventsProcessor}'s
 * <code>process</code> method.
 */
public class CloudTrailEvent {
    /** An instance of CloudTrailEventData. */
    private CloudTrailEventData eventData;

    /** An instance of CloudTrailEventMetadata. */
    private CloudTrailEventMetadata eventMetadata;

    /**
     * Initializes a CloudTrailEvent object.
     *
     * @param eventData The {@link CloudTrailEventData} to process.
     * @param eventMetadata A {@link CloudTrailEventMetadata} object that can provide delivery information about the event.
     */
    public CloudTrailEvent(CloudTrailEventData eventData, CloudTrailEventMetadata eventMetadata) {
        this.eventData = eventData;
        this.eventMetadata = eventMetadata;
    }

    /**
     * Get the {@link CloudTrailEventData} used to initialize this object.
     *
     * @return the <code>CloudTrailEventData</code> held by this instance.
     */
    public CloudTrailEventData getEventData() {
        return eventData;
    }

    /**
     * Get the {@link CloudTrailEventMetadata} used to initialize this object.
     *
     * @return the <code>CloudTrailDeliveryInfo</code>.
     */
    public CloudTrailEventMetadata getEventMetadata() {
        return eventMetadata;
    }

    /**
     * Returns a string representation of this object; useful for testing and
     * debugging.
     *
     * @return A string with the format:
     *    <code>{ eventData: "eventData", eventMetadata: "eventMetadata" }</code>.
     *    A field will not be rendered if its value is <code>null</code>.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (eventData != null)
            builder.append("eventData: ").append(eventData).append(", ");
        if (eventMetadata != null)
            builder.append("eventMetadata: ").append(eventMetadata);
        builder.append("}");
        return builder.toString();
    }

    /**
     * Returns a hash code for the object.
     *
     * @return the object's hash code value.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((eventMetadata == null) ? 0 : eventMetadata.hashCode());
        result = prime * result + ((eventData == null) ? 0 : eventData.hashCode());
        return result;
    }

    /**
     * Complares this <code>CloudTrailEvent</code> object with another.
     *
     * @return <code>true</code> if they represent the same event;
     *   <code>false</code> * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CloudTrailEvent other = (CloudTrailEvent) obj;
        if (eventMetadata == null) {
            if (other.eventMetadata != null)
                return false;
        } else if (!eventMetadata.equals(other.eventMetadata))
            return false;
        if (eventData == null) {
            if (other.eventData != null)
                return false;
        } else if (!eventData.equals(other.eventData))
            return false;
        return true;
    }
}
