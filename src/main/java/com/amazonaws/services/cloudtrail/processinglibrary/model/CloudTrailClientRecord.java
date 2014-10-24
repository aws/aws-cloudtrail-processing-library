/*******************************************************************************
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * Provides AWS CLoudTrail log information to your {@link RecordProcessor}'s <code>process</code> method.
 */
public class CloudTrailClientRecord {
    /** An instance of CloudTrailRecord. */
    private CloudTrailRecord record;

    /** An instance of AWSCloudTrailRecordDeliveryInfo. */
    private CloudTrailDeliveryInfo deliveryInfo;

    /**
     * Initializes a CloudTrailClientRecord object.
     *
     * @param record The {@link CloudTrailRecord} to process.
     * @param deliveryInfo A {@CloudTrailDeliveryInfo} object that can provide delivery information about the record.
     */
    public CloudTrailClientRecord(CloudTrailRecord record, CloudTrailDeliveryInfo deliveryInfo) {
        this.record = record;
        this.deliveryInfo = deliveryInfo;
    }

    /**
     * Get the {@link CloudTrailRecord} used to initialize this object.
     * @return the <code>CloudTrailRecord</code> held by this instance.
     */
    public CloudTrailRecord getRecord() {
        return record;
    }

    /**
     * Get the {@ CloudTrailDeliveryInfo} used to initialize this object.
     * @return the <code>CloudTrailDeliveryInfo</code>.
     */
    public CloudTrailDeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    /**
     * Returns a string representation of this object; useful for testing and
     * debugging.
     *
     * @return A string with the format:
     *    <code>{ record: "recordvalue", deliveryInfo: "deliveryinfo" }</code>.
     *    A field will not be rendered if its value is <code>null</code>.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (record != null)
            builder.append("record: ").append(record).append(", ");
        if (deliveryInfo != null)
            builder.append("deliveryInfo: ").append(deliveryInfo);
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
                + ((deliveryInfo == null) ? 0 : deliveryInfo.hashCode());
        result = prime * result + ((record == null) ? 0 : record.hashCode());
        return result;
    }

    /**
     * Complares this <code>CloudTrailClientRecord</code> object with another.
     *
     * @return <code>true</code> if they represent the same record;
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
        CloudTrailClientRecord other = (CloudTrailClientRecord) obj;
        if (deliveryInfo == null) {
            if (other.deliveryInfo != null)
                return false;
        } else if (!deliveryInfo.equals(other.deliveryInfo))
            return false;
        if (record == null) {
            if (other.record != null)
                return false;
        } else if (!record.equals(other.record))
            return false;
        return true;
    }
}
