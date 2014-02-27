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
package com.amazonaws.services.cloudtrail.clientlibrary.interfaces;

import java.util.List;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.model.ClientRecord;

/**
 * Emitter is a call back function that hands a list of AWSCloudTrailRecord to user. User can determinate 
 * what to do with those record. The emit() method is invoked after RecordFilter is being called. 
 * Records filtered by RecordFilter will not emit out. The number of records in the list is configurable 
 * throw "recordBufferSize" property. Important, we may emit an empty list records.
 */
public interface RecordsProcessor {

    /**
     * Process a list of AWSCloudTrailRecord records.
     * 
     * @param records a list of AWSCloudTrailRecord instance
     */
    public void process(List<ClientRecord> records, AWSCloudTrailClientConfiguration config);

}
