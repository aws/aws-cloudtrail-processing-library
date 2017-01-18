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

/**
 * CloudTrail log delivery information
 */
public class LogDeliveryInfo implements CloudTrailEventMetadata{
    private CloudTrailLog log;
    private int charStart;
    private int charEnd;
    private String rawEvent;

    /**
     * The log delivery information.
     *
     * @param log that event was coming from.
     * @param charStart the 0-based location of the event's starting character "{" and -1 when enableRawEventInfo is false.
     * @param charEnd the 0-based location of the event's ending character "}" and -1 when enableRawEventInfo is false.
     * @param rawEvent the CloudTrail event in raw String - as it is in the log file and null when enableRawEventInfo is false.
     */
    public LogDeliveryInfo(CloudTrailLog log, int charStart, int charEnd, String rawEvent) {
        this.log = log;
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.rawEvent = rawEvent;
    }

    /**
     * @return the CloudTrail log
     */
    public CloudTrailLog getLog() {
        return log;
    }

    /**
     * @return the location of the event's starting character
     */
    public long getCharStart() {
        return charStart;
    }

    /**
     * @return the location of the event's ending character
     */
    public long getCharEnd() {
        return charEnd;
    }

    /**
     * @return the CloudTrail event in raw String - as it is in the log file
     */
    public String getRawEvent() {
        return rawEvent;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (log != null) {
            builder.append("log: ");
            builder.append(log);
            builder.append(", ");
        }
        builder.append("charStart: ");
        builder.append(charStart);
        builder.append(", charEnd: ");
        builder.append(charEnd);
        builder.append(", ");
        if (rawEvent != null) {
            builder.append("rawEvent: ");
            builder.append(rawEvent);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (charEnd ^ (charEnd >>> 32));
        result = prime * result + (int) (charStart ^ (charStart >>> 32));
        result = prime * result + ((log == null) ? 0 : log.hashCode());
        result = prime * result + ((rawEvent == null) ? 0 : rawEvent.hashCode());
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
        LogDeliveryInfo other = (LogDeliveryInfo) obj;
        if (charEnd != other.charEnd)
            return false;
        if (charStart != other.charStart)
            return false;
        if (log == null) {
            if (other.log != null)
                return false;
        } else if (!log.equals(other.log))
            return false;
        if (rawEvent == null) {
            if (other.rawEvent != null)
                return false;
        } else if (!rawEvent.equals(other.rawEvent))
            return false;
        return true;
    }
}
