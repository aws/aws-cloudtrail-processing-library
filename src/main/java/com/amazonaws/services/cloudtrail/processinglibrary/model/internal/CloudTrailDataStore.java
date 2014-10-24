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

package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal use only. Generic data store for CloudTrail model.
 */
public class CloudTrailDataStore {
    /**
     * Store underlying data into a map.
     */
    private Map<String, Object> recordStore;

    public CloudTrailDataStore() {
        this.recordStore = new HashMap<>();
    }

    /**
     * Internal use only. Add key value pair to underlying data store.
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        this.recordStore.put(key, value);
    }

    /**
     * To retrieve value based on key from underlying data store.
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return this.recordStore.get(key);
    }

    /**
     * Provides check for verifying if data store has value associated with key or not.
     *
     * @param key
     * @return
     */
    public boolean has(String key) {
        return this.recordStore.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (recordStore != null) {
            builder.append(this.getClass().getSimpleName());
            builder.append(": ");
            builder.append(recordStore);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((recordStore == null) ? 0 : recordStore.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CloudTrailDataStore other = (CloudTrailDataStore) obj;
        if (recordStore == null) {
            if (other.recordStore != null)
                return false;
        } else if (!recordStore.equals(other.recordStore))
            return false;
        return true;
    }


}
