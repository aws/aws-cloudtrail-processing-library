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

package com.amazonaws.services.cloudtrail.processinglibrary.interfaces;

import java.util.List;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;

/**
 * Provides a callback method that is used by an {@link
 * AWSCloudTrailProcessingExecutor} to deliver AWS CloudTrail records for
 * processing.
 * <p>
 * The <code>process()</code> method is invoked after the optional {@link
 * RecordFilter}'s callback is invoked. If the record was rejected by the
 * <code>RecordFilter</code>, then it will not be sent to
 * <code>process()</code>.
 * <p>
 * The number of records in the list is configurable through the
 * <code>recordBufferSize</code> property.
 *
 * @see ProcessingConfiguraton
 */
public interface RecordsProcessor {
    /**
     * A callback method that processes a list of <code>CloudTrailRecord</code>
     * records.
     * <p>
     * This callback is called by an {@link AWSCloudTrailProcessingExecutor}
     * when it has records to process.
     *
     * @param records a list of {@link CloudTrailRecord} objects.
     * @throws CallbackException  if an error occurs while processing records.
     */
    public void process(List<CloudTrailClientRecord> records) throws CallbackException;
}
