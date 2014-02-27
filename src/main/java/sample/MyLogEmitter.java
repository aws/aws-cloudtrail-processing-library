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
package sample;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.clientlibrary.model.ClientRecord;

public class MyLogEmitter implements RecordsProcessor {
	private static final Log MyLogEmitter = LogFactory.getLog(MyLogEmitter.class);
	
	public void process(List<ClientRecord> records, AWSCloudTrailClientConfiguration config) {
//		int i = 0;
//		for (ClientRecord record : records) {
//			MyLogEmitter.info("Record " + i++ + " : " + record.getRecord() + " | DeliveryInfo: " + record.getDeliveryInfo());
//		}
		MyLogEmitter.info("====> Total number of Records " + records.size());
	}

}
