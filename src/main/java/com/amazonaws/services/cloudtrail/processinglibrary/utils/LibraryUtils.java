/*******************************************************************************
 * Copyright (c) 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
package com.amazonaws.services.cloudtrail.processinglibrary.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class LibraryUtils {

    private static final String UNDER_SCORE = "_";
    private static final String FORWARD_SLASH = "/";
    private static final String AMAZONAWS_COM = ".amazonaws.com/";
    private static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String UTC_TIME_ZONE = "UTC";

    /**
     * Check argument is not null, throw exception when condition failed.
     * @param argument
     */
    public static void checkArgumentNotNull(Object argument, String message) {
        if (argument == null) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Check condition, if satisfied, then {@link IllegalStateException} will be thrown
     * @param condition
     * @param message
     */
    public static void checkCondition(boolean condition, String message) {
        if (condition) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Convenient function to convert InputSteam to byte array.
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead = 0;
        byte[] bytes = new byte[1024];
        while ((nRead = inputStream.read(bytes, 0, 1024)) != -1) {
            buffer.write(bytes, 0, nRead);
        }
        return buffer.toByteArray();
    }

    /**
     * Split a http representation of s3 url to bucket name and object key. Example:
     * input: s3ObjectHttpUrl = http://s3-us-west-2.amazonaws.com/mybucket/myobjectpath1/myobjectpath2/myobject.extension
     * output: {"mybucket", "myobjectpath1/myobjectpath2/myobject.extension"}
     *
     * @param s3ObjectHttpUrl
     * @return
     */
    public static String[] toBucketNameObjectKey(String s3ObjectHttpUrl) {
        if (s3ObjectHttpUrl == null) {
            return null;
        }

        int start = s3ObjectHttpUrl.indexOf(AMAZONAWS_COM);
        int length = s3ObjectHttpUrl.length();

        if (start != -1) {
            String bucketNameAndObjectKey = s3ObjectHttpUrl.substring(start + AMAZONAWS_COM.length(), length);
            return bucketNameAndObjectKey.split(FORWARD_SLASH, 2);
        }
        return null;

    }

    /**
     * S3 object key contains account Id, extract it. Example:
     * input: https://s3-us-west-2.amazonaws.com/mybucket/AWSLogs/123456789012/CloudTrail/us-east-1/2014/02/14/123456789012_CloudTrail_us-east-1_20140214T2230Z_K0UsfksWvF8TBJZy.json.gz
     * output: 1234567890
     *
     * @param objectKey
     * @return
     */
    public static String extractAccountIdFromObjectKey(String objectKey) {
        if (objectKey == null) {
            return null;
        }

        int start = objectKey.lastIndexOf(FORWARD_SLASH);

        if (start != -1) {
            int end = objectKey.indexOf(UNDER_SCORE, start + FORWARD_SLASH.length());

            if (end != -1) {
                return objectKey.substring(start + FORWARD_SLASH.length(), end);
            }
        }
        return null;
    }

    /**
     * SimpleDateFormat is not thread safe, and define it as static ThreadLocal to
     * synchronize is less expensive to create SimpleDateFormat object every time.
     */
    private static ThreadLocal<SimpleDateFormat> utcSdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat(UTC_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE));
            return sdf;
        }
    };

    public static SimpleDateFormat getUtcSdf() {
        return utcSdf.get();
    }
}
