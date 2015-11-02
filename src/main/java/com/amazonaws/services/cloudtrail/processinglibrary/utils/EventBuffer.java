/*******************************************************************************
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.amazonaws.services.cloudtrail.processinglibrary.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a buffer-like store for AWS CloudTrail events.
 */
public class EventBuffer<T> {
    private List<T> bufferedEvents;
    private int bufferSize;

    /**
     * Initialize a new <code>EventBuffer</code>.
     *
     * @param bufferSize the number of events that can be held in the buffer.
     */
    public EventBuffer(final int bufferSize) {
        LibraryUtils.checkCondition(bufferSize < 1, "Event Buffer size cannot be " + bufferSize + ", must be at lease 1.");

        this.bufferedEvents = new LinkedList<T>();
        this.bufferSize = bufferSize;
    }

    /**
     * Indicates whether the buffer has reached the number of events configured in the constructor.
     *
     * @return <code>true</code> if the current buffer is full; <code>false</code> otherwise.
     */
    public boolean isBufferFull() {
        return bufferedEvents.size() >= this.bufferSize;
    }

    /**
     * Add a event to the buffer.
     *
     * @param event An object of the type configured for this buffer.
     */
    public void addEvent(T event) {
        this.bufferedEvents.add(event);
    }

    /**
     * Get a list of objects held by the buffer.
     * <p>
     * The number of returned objects will be from zero to the configured buffer size.
     *
     * @return a <a href="http://docs.oracle.com/javase/7/docs/api/java/util/List.html">List</a> containing the buffered
     *     objects.
     */
    public List<T> getEvents() {
        List<T> returnEvents = new ArrayList<T>();

        if (this.bufferedEvents.isEmpty()) {
            return returnEvents;
        }

        int returnSize = this.isBufferFull() ? this.bufferSize : this.bufferedEvents.size();

        for (int i = 0 ; i < returnSize ; i++) {
            returnEvents.add(bufferedEvents.remove(0));
        }

        return returnEvents;
    }
}
