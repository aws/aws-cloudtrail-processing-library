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

package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.CloudTrailRecordField;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.Resource;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RawLogDeliveryRecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecordSerializerBadDataTest {

    /**
     * In the test file userIdentity is missing close brace.
     */
    private static final String testFilePathBadJsonFormat = "/resources/SerializerTestFileBadJsonFormat.json";

    /**
     * In the test file all field name are suffixed by "Wrong". Field names are not what CloudTrail published.
     */
    private static final String testFilePathBadFieldName = "/resources/SerializerTestFileBadFieldName.json";

    private static final String testFilePathMissField = "/resources/SerializerTestFileMissField.json";

    /**
     * In the test file all fields are null
     */
    private static final String testFilePathAllFieldsNull = "/resources/SerializerAllFieldsNull.json";

    /**
     * Various User Identity Errors
     */
    private static final String testFilePathVariousUserIdentity = "/resources/SerializerVariousUserIdentityTestFile.json";

    private static Date matchDate;

    static {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            matchDate = isoFormat.parse("2013-11-01T00:00:00Z");
        } catch (ParseException e) {

        }
    }

    @Test
    public void testBadJsonFormat() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testFilePathBadJsonFormat);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new RawLogDeliveryRecordSerializer(logFile, new CloudTrailLog(null, null), jsonParser);

        try {
            this.testUserIdentityMissCloseBrace(serializer, "1");
            assertTrue(false);
        } catch (JsonParseException e) {
            assertTrue(true);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBadFieldName() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testFilePathBadFieldName);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new RawLogDeliveryRecordSerializer(logFile, new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldNull(record);
        Verifier.verifyRecordTopLevelFieldThroughDataStore(record, "Wrong", "1");
        record = this.testRecordGood(serializer, "2");
        Verifier.verifyResources((List<Resource>)record.get("resources"), 2);
    }

    @Test
    public void testSerializerBadFieldName() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testFilePathMissField);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new RawLogDeliveryRecordSerializer(logFile, new CloudTrailLog(null, null), jsonParser);

        CloudTrailRecord record = this.testRecordGood(serializer, "1");
        Verifier.verifyResources((List<Resource>) record.getResources(), 2);
        record = this.testRecordGood(serializer, "2");
        Verifier.verifyResources((List<Resource>) record.getResources(), 0);
        record = this.testUserIdentityMissFields(serializer, "3");
        assertNull((List<Resource>) record.getResources());
        this.testBadEventDate(serializer, "4");
        this.testNoNextRecord(serializer);
    }

    @Test
    public void testSerializerAllFieldsNull() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testFilePathAllFieldsNull);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new RawLogDeliveryRecordSerializer(logFile, new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldNull(record);

        assertTrue(serializer.hasNextRecord());
        clientRecord = serializer.getNextRecord();
        record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, "2");
        Verifier.verifyRecordTopLevelFieldThroughDataStore(record, "", "2");
        Verifier.verifyResources((List<Resource>) record.getResources(), 3);
        assertEquals(matchDate, record.get(CloudTrailRecordField.eventTime.name()));
        assertEquals("userIdentityAccountId2", record.get(CloudTrailRecordField.accountId.name()));
        serializer.close();
    }

    @Test
    public void testVariousUserIdentity() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testFilePathVariousUserIdentity);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new RawLogDeliveryRecordSerializer(logFile, new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyResources((List<Resource>) record.getResources(), 3);
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, "1");
        serializer.close();
    }

    /**
     * If process a record throw exception, rest of records in this file will not be processed.
     *
     * @param serializer
     * @param suffix
     * @throws IOException
     */
    private void testNoNextRecord(RecordSerializer serializer) throws IOException {
        assertFalse(serializer.hasNextRecord());
    }

    private CloudTrailRecord testRecordGood(RecordSerializer serializer, String suffix) throws IOException {
        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);
        Verifier.verifyUserIdentifyTopLevelField(record, suffix);
        Verifier.verifyRawRecordNotNull(clientRecord);
        return record;
    }

    private CloudTrailRecord testUserIdentityMissFields(RecordSerializer serializer, String suffix) throws IOException {
        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        assertEquals("1." + suffix, record.getEventVersion());
        assertEquals(matchDate, record.getEventTime());
        assertEquals("eventSource" + suffix, record.getEventSource());
        assertEquals("eventName" + suffix, record.getEventName());
        assertEquals("awsRegion" + suffix, record.getAwsRegion());
        assertEquals("sourceIPAddress" + suffix, record.getSourceIPAddress());
        assertEquals("userAgent" + suffix, record.getUserAgent());
        assertEquals("errorCode" + suffix, record.getErrorCode());
        assertEquals("errorMessage" + suffix, record.getErrorMessage());
        Verifier.verifyRawRecordNotNull(clientRecord);
        return record;
    }

    private void testUserIdentityMissCloseBrace(RecordSerializer serializer, String suffix) throws IOException {
        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        assertEquals("1." + suffix, record.getEventVersion());
        assertEquals("userIdentityAccountId" + suffix, record.getAccountId());
        assertNotNull(record.getUserIdentity());
        assertEquals("{\"loggingEnabled\":false}", record.getResponseElements());
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testBadEventDate(RecordSerializer serializer, String suffix) throws IOException {
        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = null;
        try {
            clientRecord = serializer.getNextRecord();
            assertTrue(false);
        } catch (IOException e) {

        }
        assertTrue(clientRecord == null);
    }
}
