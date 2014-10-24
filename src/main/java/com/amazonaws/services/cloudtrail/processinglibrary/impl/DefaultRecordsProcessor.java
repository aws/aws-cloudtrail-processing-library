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

package com.amazonaws.services.cloudtrail.processinglibrary.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;

/**
 * Default implementation of RecordsProcesssor that simply log each record.
 */
public class DefaultRecordsProcessor implements RecordsProcessor {
    private static final Log logger = LogFactory.getLog(DefaultExceptionHandler.class);

    @Override
    public void process(List<CloudTrailClientRecord> records) {
        for (CloudTrailClientRecord record : records) {
            logger.info(record);
        }
    }
}
