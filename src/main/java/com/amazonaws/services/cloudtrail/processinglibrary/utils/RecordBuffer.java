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

package com.amazonaws.services.cloudtrail.processinglibrary.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provide buffer-like implementation.
 *
 * @param <T>
 */
public class RecordBuffer<T> {
    private List<T> bufferedRecords;
    private int bufferSize;

    public RecordBuffer(final int bufferSize) {
        LibraryUtils.checkCondition(bufferSize < 1, "Record Buffer size cannot be " + bufferSize + ", must be at lease 1.");

        this.bufferedRecords = new LinkedList<T>();
        this.bufferSize = bufferSize;
    }

    /**
     * Buffer reaches its size.
     * @return true if current buffer reaches this buffer size.
     */
    public boolean isBufferFull() {
        return bufferedRecords.size() >= this.bufferSize;
    }

    /**
     * Add a record to the buffer.
     * @param record An object
     */
    public void addRecord(T record) {
        this.bufferedRecords.add(record);
    }

    /**
     * Return a list of object upto bufferSize if buffer is full, otherwise return whatever in the buffer.
     * @return
     */
    public List<T> getRecords() {
        List<T> returnRecords = new ArrayList<T>();

        if (this.bufferedRecords.isEmpty()) {
            return returnRecords;
        }

        int returnSize = this.isBufferFull() ? this.bufferSize : this.bufferedRecords.size();

        for (int i = 0 ; i < returnSize ; i++) {
            returnRecords.add(bufferedRecords.remove(0));
        }

        return returnRecords;
    }
}
