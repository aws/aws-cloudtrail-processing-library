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

package com.amazonaws.services.cloudtrail.processinglibrary.model;

/**
 * Enumeration of source attribute key names
 */
public enum SourceAttributeKeys {
    ACCOUNT_ID("accountId"),
    APPROXIMATE_FIRST_RECEIVE_TIMESTAMP("ApproximateFirstReceiveTimestamp"),
    APPROXIMATE_RECEIVE_COUNT("ApproximateReceiveCount"),
    SEND_TIMESTAMP("SentTimestamp"),
    SENDER_ID("SenderId");

    private final String attributeKey;

    private SourceAttributeKeys(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public static SourceAttributeKeys fromAttributeKeyName(String attributeKeyName) {
        for (SourceAttributeKeys attributeKey : SourceAttributeKeys.values()) {
            if (attributeKeyName.equals(attributeKey.getAttributeKey())) {
                return attributeKey;
            }
        }
        throw new IllegalArgumentException("Cannot create enum from " + attributeKeyName + " value!");
    }
}