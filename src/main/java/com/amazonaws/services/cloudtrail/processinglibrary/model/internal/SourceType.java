package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.sqs.model.Message;

/**
 *  Enumeration of type of {@link CloudTrailSource}.
 *  <p>
 *      If there are multiple source types in {@link Message}, the priority of source type is in the following order:
 *  <code>CloudTrailLog</code>, <code>Other</code>.
 *  </p>
 *
 */
public enum SourceType {
    /**
     * CloudTrail log file.
     */
    CloudTrailLog,

    /**
     * Non-CloudTrail log file.
     */
    Other
}
