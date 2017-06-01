package com.amazonaws.services.cloudtrail.processinglibrary.utils;

import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.SourceType;

import java.util.regex.Pattern;

/**
 * Identify the source type by checking the given source string and event name if applied. Specifically,
 * the <code>source</code> is usually the S3 object key and <code>eventName</code> defined by Amazon S3.
 * The following are valid source types: CloudTrailLog | Other
 */
public class SourceIdentifier {
    private static final String PUT_EVENT = "ObjectCreated:Put";

    /**
     * Regex for the name format of CloudTrail log file objects that deliver to AWS S3 bucket:
     * AccountID_CloudTrail_RegionName_YYYYMMDDTHHmmZ_UniqueString.FileNameFormat
     *
     * We need this regex to filter out non-CloudTrail log files as it is possible that S3 send other object notifications
     */
    private static final Pattern CT_LOGFILE_PATTERN = Pattern.compile(".*\\d+_CloudTrail_[\\w\\-]+_\\d{8}T\\d{4}Z_[\\w]+\\.json\\.gz");

    /**
     * Identify the source type.
     * @param source the name of S3 object which is put to the bucket.
     * @return {@link SourceType}
     */
    public SourceType identify(String source) {
        return getCloudTrailSourceType(source);
    }

    /**
     * Identify the source type with event action.
     * @param source the S3 object name
     * @param eventName the event name defined by Amazon S3.
     * @return {@link SourceType}
     */
    public SourceType identifyWithEventName(String source, String eventName) {
        if (PUT_EVENT.equals(eventName)) {
            return getCloudTrailSourceType(source);
        }
        return SourceType.Other;
    }


    private SourceType getCloudTrailSourceType(String source) {
        if (CT_LOGFILE_PATTERN.matcher(source).matches()) {
            return SourceType.CloudTrailLog;
        }
        return SourceType.Other;
    }

}
