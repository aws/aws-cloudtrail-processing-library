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

package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal use only.
 * <p>
 * Generic data store for the AWS CloudTrail model.
 */
public class CloudTrailDataStore {
    /**
     * Store underlying data into a map.
     */
    private Map<String, Object> dataStore;

    public CloudTrailDataStore() {
        this.dataStore = new HashMap<>();
    }

    /**
     * Internal use only.
     * <p>
     * Add a key/value pair to the underlying data store.
     *
     * @param key the key used to index the value.
     * @param value the value that will be associated with the provided key.
     */
    public void add(String key, Object value) {
        this.dataStore.put(key, value);
    }

    /**
     * Internal use only.
     * <p>
     * Retrieve a value associated with a key from the underlying data store.
     *
     * @param key the key in data store
     * @return the value associated with the provided key.
     */
    public Object get(String key) {
        return this.dataStore.get(key);
    }

    /**
     * Internal use only.
     * <p>
     * Verifies if the data store has a value associated with a particluar key.
     *
     * @param key the key in the data store to query.
     * @return <code>true</code> if the provided key exists in the data store; <code>false</code> otherwise.
     */
    public boolean has(String key) {
        return this.dataStore.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (dataStore != null) {
            builder.append(this.getClass().getSimpleName());
            builder.append(": ");
            builder.append(dataStore);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataStore == null) ? 0 : dataStore.hashCode());
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
        if (dataStore == null) {
            if (other.dataStore != null)
                return false;
        } else if (!dataStore.equals(other.dataStore))
            return false;
        return true;
    }
}
