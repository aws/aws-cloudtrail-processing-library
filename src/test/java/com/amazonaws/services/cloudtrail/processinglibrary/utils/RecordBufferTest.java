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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.utils.RecordBuffer;

public class RecordBufferTest {

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testEmptyBuffer() {
        RecordBuffer rb = new RecordBuffer<Integer>(3);

        for (int i = 0; i < 7; i++) {
            rb.addRecord(new Integer(i));
        }

        assertTrue(rb.isBufferFull());
        this.matchValue(Arrays.asList(0, 1, 2), rb.getRecords());

        assertTrue(rb.isBufferFull());
        this.matchValue(Arrays.asList(3, 4, 5), rb.getRecords());

        assertFalse(rb.isBufferFull());
        this.matchValue(Arrays.asList(6), rb.getRecords());

        assertFalse(rb.isBufferFull());
    }

    private <T> boolean matchValue(List<T> one, List<T> two) {
        if (one.size() != two.size()) {
            return false;
        }

        for (int i = 0; i < one.size(); i++) {
            if (one.get(i) != two.get(i)) {
                return false;
            }
        }
        return true;
    }

}
