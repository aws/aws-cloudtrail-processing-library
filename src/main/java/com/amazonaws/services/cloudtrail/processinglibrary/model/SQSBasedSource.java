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

package com.amazonaws.services.cloudtrail.processinglibrary.model;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.sqs.model.Message;

public class SQSBasedSource implements CloudTrailSource{
    /**
     * List of CloudTrailLogs inside this source
     */
    private final List<CloudTrailLog> logs;

    /**
     * The SQS message polled from queue.
     */
    private final Message sqsMessage;

    /**
     * This method return a Map of String (key) and String (value). This map contains standard SQS message
     * attributes, i.e. SenderId, SentTimestamp, ApproximateReceiveCount, and/or
     * ApproximateFirstReceiveTimestamp, etc as well as attributes CloudTrail published.
     *
     * @param sqsMessage
     * @param logs
     */
    public SQSBasedSource(Message sqsMessage, List<CloudTrailLog> logs) {
        this.sqsMessage = sqsMessage;
        this.logs = logs;
    }

    /**
     * Retrieve the CloudTrailSource attributes
     */
    @Override
    public Map<String, String> getSourceAttributes() {
        return this.sqsMessage.getAttributes();
    }

    /**
     * @return the SQS message
     */
    public Message getSqsMessage() {
        return sqsMessage;
    }

    /**
     * @return the list of CloudTrailLog retrieved from the source
     */
    public List<CloudTrailLog> getLogs() {
        return logs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (logs != null) {
            builder.append("logs: ");
            builder.append(logs);
            builder.append(", ");
        }
        if (sqsMessage != null) {
            builder.append("sqsMessage: ");
            builder.append(sqsMessage);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((logs == null) ? 0 : logs.hashCode());
        result = prime * result + ((sqsMessage == null) ? 0 : sqsMessage.hashCode());
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
        SQSBasedSource other = (SQSBasedSource) obj;
        if (logs == null) {
            if (other.logs != null)
                return false;
        } else if (!logs.equals(other.logs))
            return false;
        if (sqsMessage == null) {
            if (other.sqsMessage != null)
                return false;
        } else if (!sqsMessage.equals(other.sqsMessage))
            return false;
        return true;
    }
}
