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
package com.amazonaws.services.cloudtrail.clientlibrary.serializer;

import java.io.IOException;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailClientRecord;
import com.fasterxml.jackson.core.JsonParseException;

/**
 * AWSCloudTrailSerializer is an interface provides a set of methods to serialize AWS CloudTrail log files in a
 * streaming fashion.
 */
public interface AWSCloudTrailRecordSerializer extends AutoCloseable {
    /**
     * Whether there is more records in the current file.
     * @return
     * @throws IOException
     */
    public boolean hasNextRecord() throws IOException;


    /**
     * Get next records, this one should be call after verify there is more records through hasNextRecord.
     * @return
     * @throws MessageParsingException
     * @throws IOException
     * @throws JsonParseException
     */
    public CloudTrailClientRecord getNextRecord() throws IOException;

    /**
     * Close the underlining input stream
     * @throws IOException
     */
    public void close() throws IOException;
}
