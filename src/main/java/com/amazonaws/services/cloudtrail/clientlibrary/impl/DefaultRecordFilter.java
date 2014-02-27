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
package com.amazonaws.services.cloudtrail.clientlibrary.impl;

import com.amazonaws.services.cloudtrail.clientlibrary.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.model.ClientRecord;

/**
 * Default all pass RecordFilter implementation.
 */
public class DefaultRecordFilter implements RecordFilter {

    @Override
    public boolean filterRecord(ClientRecord record, AWSCloudTrailClientConfiguration config) {
        return true;
    }
}
