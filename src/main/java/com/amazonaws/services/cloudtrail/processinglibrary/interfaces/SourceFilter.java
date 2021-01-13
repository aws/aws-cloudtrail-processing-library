/*******************************************************************************
 * Copyright 2010-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;

/**
 * SourceFilter is a call back function that hands a CloudTrailSource to user. User can
 * determinate whether want to process this source. The filter() method is invoked after
 * polled SQS message from SQS queue and before process events. For performance, CloudTrailSource
 * is not cloned, caller should not change the content of source.
 */
public interface SourceFilter{

    /**
     * A callback method used to filter a {@link CloudTrailSource} prior to process.
     * <p>
     * For performance, the source object is not a copy; you should only filter the source here, not change its contents.
     * </p>
     * @param source The {@link CloudTrailSource} to filter.
     * @return <code>true</code> if the source should be processed by the {@link SourceFilter}.
     * @throws CallbackException When error happened during filtering <code>source</code>. CPL will eventually hand this
     * exception back to <code>ExceptionHandler</code>.
     */
    public boolean filterSource(final CloudTrailSource source) throws CallbackException;

}
