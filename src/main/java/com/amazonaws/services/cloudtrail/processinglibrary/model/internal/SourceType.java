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
 *******************************************************************************/

package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.sqs.model.Message;

/**
 *  Enumeration of type of {@link CloudTrailSource}.
 *  <p>
 *      If there are multiple source types in {@link Message}, the priority of source type is in the following order:
 *  <code>CloudTrailLog</code>, <code>Other</code>.
 *  </p>
 *
 */
public enum SourceType {
    /**
     * CloudTrail log file.
     */
    CloudTrailLog,

    /**
     * CloudTrail Validation Message.
     */
    CloudTrailValidationMessage,

    /**
     * Non-CloudTrail log file.
     */
    Other
}
