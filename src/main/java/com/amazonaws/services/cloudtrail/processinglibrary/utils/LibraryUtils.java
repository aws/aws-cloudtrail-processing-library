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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Utility methods used by the AWS CloudTrail Processing Library.
 */
public class LibraryUtils {

    private static final String UNDER_SCORE = "_";
    private static final String FORWARD_SLASH = "/";
    private static final String AMAZONAWS_COM = ".amazonaws.com/";
    private static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String UTC_TIME_ZONE = "UTC";

    /**
     * Check that an object is not <code>null</code>; throw an exception if it
     * is.
     *
     * @param argument the Object to check.
     * @param message a description string that will be sent in the exception.
     * @throws IllegalStateException
     */
    public static void checkArgumentNotNull(Object argument, String message) {
        if (argument == null) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Check a conditional value or expression, if <code>true</code>, throw an
     * exception.
     *
     * @param condition the boolean value or expression to check.
     * @param message a description string that will be sent in the exception.
     * @throws IllegalStateException
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
     * Split an HTTP representation of an Amazon S3 URL to bucket name and object key.
     * <p>
     * For example:
     * <pre>
     * input: s3ObjectHttpUrl = http://s3-us-west-2.amazonaws.com/mybucket/myobjectpath1/myobjectpath2/myobject.extension
     * output: {"mybucket", "myobjectpath1/myobjectpath2/myobject.extension"}
     * </pre>
     *
     * @param s3ObjectHttpUrl the URL of the S3 object to split.
     * @return a two-element string array: the first element is the bucket name,
     *    and the second element is the object key.
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
     * Excract the account ID from an S3 object key.
     * <p>
     * For example:
     * <pre>
     * input: https://s3-us-west-2.amazonaws.com/mybucket/AWSLogs/123456789012/CloudTrail/us-east-1/2014/02/14/123456789012_CloudTrail_us-east-1_20140214T2230Z_K0UsfksWvF8TBJZy.json.gz
     * output: 1234567890
     * </pre>
     *
     * @param objectKey The object key to query.
     * @return the account ID used to access the object.
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

    /**
     * Returns a timestamp.
     */
    public static SimpleDateFormat getUtcSdf() {
        return utcSdf.get();
    }
}
