/*******************************************************************************
 * Copyright (c) 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.amazonaws.services.cloudtrail.processinglibrary.model;


/**
 * This class wrap AWSCloudTrailRecord and AWSCloudTrailRecordDeliveryInfo.
 *
 */
public class CloudTrailClientRecord {
    /**
     * An instance of AWSCloudTrailRecord.
     */
    private CloudTrailRecord record;

    /**
     * An instance of AWSCloudTrailRecordDeliveryInfo.
     */
    private CloudTrailDeliveryInfo deliveryInfo;

    /**
     * AWSCloudTrailClientRecord constructor
     *
     * @param record
     * @param deliveryInfo
     */
    public CloudTrailClientRecord(CloudTrailRecord record, CloudTrailDeliveryInfo deliveryInfo) {
        this.record = record;
        this.deliveryInfo = deliveryInfo;
    }

    /**
     * Get an instance of AWSCloudTrailRecord
     * @return
     */
    public CloudTrailRecord getRecord() {
        return record;
    }

    /**
     * Get an instance of AWSCloudTrailRecordDeliveryInfo
     * @return
     */
    public CloudTrailDeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    /**
     * Returns a string representation of this object; useful for testing and debugging.
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((deliveryInfo == null) ? 0 : deliveryInfo.hashCode());
        result = prime * result + ((record == null) ? 0 : record.hashCode());
        return result;
    }

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
