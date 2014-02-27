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
package com.amazonaws.services.cloudtrail.clientlibrary.model;

/**
 * This class wrap AWSCloudTrailRecord and AWSCloudTrailRecordDeliveryInfo.
 *
 */
public class ClientRecord {
	/**
	 * An instance of AWSCloudTrailRecord.
	 */
	private CloudTrailRecord record;
	
	/**
	 * An instance of AWSCloudTrailRecordDeliveryInfo.
	 */
	private RecordDeliveryInfo deliveryInfo;
	
	/**
	 * AWSCloudTrailClientRecord constructor
	 * 
	 * @param record
	 * @param deliveryInfo
	 */
	public ClientRecord(CloudTrailRecord record,
			RecordDeliveryInfo deliveryInfo) {
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
	public RecordDeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	/**
	 * Converts this AWSCloudTrailClientRecord object to a String of the form.
	 */
	@Override
	public String toString() {
		return "AWSCloudTrailClientRecord [record=" + record
				+ ", deliveryInfo=" + deliveryInfo + "]";
	}
	
	
}
