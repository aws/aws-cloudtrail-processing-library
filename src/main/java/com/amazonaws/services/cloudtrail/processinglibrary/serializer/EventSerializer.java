/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import java.io.IOException;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;

/**
 * AWSCloudTrailSerializer is an interface provides a set of methods to serialize AWS CloudTrail log files in a
 * streaming fashion.
 */
public interface EventSerializer extends AutoCloseable {
    /**
     * Indicates if there are more events in the current log to serialize.
     *
     * @return <code>true</code> if there are more events to serialize; <code>false</code> otherwise.
     * @throws IOException if the log could not be read.
     */
    public boolean hasNextEvent() throws IOException;


    /**
     * Get the next event in the log, this one should be called after verifying that there are more events by using
     * <code>hasNextEvent()</code>.
     *
     * @return the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent}.
     * @throws IOException if the log could not be read.
     */
    public CloudTrailEvent getNextEvent() throws IOException;

    /**
     * Close the underlying input stream
     *
     * @throws IOException if the input stream could not be accessed or closed.
     */
    public void close() throws IOException;
}
