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

package sample;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;

public class SampleRecordsProcessor implements RecordsProcessor {
    private static final Log logger = LogFactory.getLog(SampleRecordsProcessor.class);

    public void process(List<CloudTrailClientRecord> records) {
        int i = 0;
        for (CloudTrailClientRecord record : records) {
            validateRecord(record);
            logger.info(String.format("Process record %d : %s", i++, record.getRecord()));
        }
    }

    /**
     * Do simple validation before processing.
     *
     * @param record to validate
     */
    private void validateRecord(CloudTrailClientRecord record) {
        if (record.getRecord().getAccountId() == null) {
            logger.error(String.format("Record %s doesn't have account ID.", record.getRecord()));
        }

        // more validation here...
    }
}