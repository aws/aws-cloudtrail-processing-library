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

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;

/**
 * Provides a callback method used by an {@link AWSCloudTrailProcessingExecutor}
 * to determine whether or not to process a record.
 * <p>
 * If <code>filterRecord()</code> returns <code>false</code>, then the record is
 * not sent to the {@link RecordsProcessor} for further processing.
 */
public interface RecordFilter{

    /**
     * A callback method used to filter a AWS CloudTrail record prior to
     * processing.
     * <p>
     * For performance, the record object is not a copy; you should only filter
     * the record here, not change its contents.
     *
     * @param record the {@link CloudTrailClientRecord} to filter.
     * @return <code>true</code> if the record should be processed by the {@link
     *    RecordsProcessor}.
     * @throws CallbackException if an error occurs while filtering.
     */
    public boolean filterRecord(CloudTrailClientRecord record) throws CallbackException;

}
