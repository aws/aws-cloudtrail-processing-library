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

package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.sqs.model.Message;

import java.io.IOException;

/**
 * Interface for getting CloudTrail log file information from {@link CloudTrailSource}. Implementations can parse
 * messages polled from SQS queue for extracting CloudTrail log file information. The following are provided implementations:
 * <p>
 *     {@link CloudTrailSourceSerializer}, {@link S3SourceSerializer}, {@link S3SNSSourceSerializer}, {@link SourceSerializerChain}.
 * </p>
 */
public interface SourceSerializer {

    /**
     * Get CloudTrail log file information by parsing single SQS message.
     *
     * @param  sqsMessage The message polled from SQS queue.
     * @return {@link CloudTrailSource} that contains log file information.
     * @throws IOException If <code>sqsMessage</code> is unrecognized.
     */
    public CloudTrailSource getSource(Message sqsMessage) throws IOException;
}
