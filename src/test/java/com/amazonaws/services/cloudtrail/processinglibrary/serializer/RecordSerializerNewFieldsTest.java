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
package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.CloudTrailRecordField;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.Resource;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.DefaultRecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecordSerializerNewFieldsTest {

    private static final String testNewFieldName = "/resources/SerializerTestFileNewFieldName.json";

    private static final String testNewFieldsInRoot = "/resources/SerializerTestFileNewFieldsInRoot.json";

    private static final String testNewFieldsInUserIdentity = "/resources/SerializerTestFileNewFieldsInUserIdentity.json";

    private static final String testNewFieldsAtRandomPlaces = "/resources/SerializerTestFileNewFieldAtRandomPlaces.json";

    @Test
    public void testNewFieldName() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testNewFieldName);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new DefaultRecordSerializer(new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldNull(record);
        Verifier.verifyRecordTopLevelFieldThroughDataStore(record, "1", "1");
        assertNotNull(record.get(CloudTrailRecordField.resources.name() + "1"));

        assertTrue(serializer.hasNextRecord());
        clientRecord = serializer.getNextRecord();
        record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, "2");
        Verifier.verifyResources((List<Resource>) record.getResources(), 0);
        serializer.close();
    }

    @Test
    public void testNewFieldNameInRoot() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testNewFieldsInRoot);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new DefaultRecordSerializer(new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughDataStore(record, "", "1");
        assertEquals("randomValue", (String) record.get("randomField2"));
        assertEquals("{}", (String) record.get("randomField1"));
        assertEquals("[\"value1\",\"value2\",\"value3\"]", (String) record.get("randomField3"));
        Verifier.verifyResources((List<Resource>) record.getResources(), 3);

        assertTrue(serializer.hasNextRecord());
        clientRecord = serializer.getNextRecord();
        record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, "2");
        Verifier.verifyResources((List<Resource>) record.getResources(), 3);
        serializer.close();
    }

    @Test
    public void testNewFieldNameInUserIdentity() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testNewFieldsInUserIdentity);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new DefaultRecordSerializer(new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughDataStore(record, "", "1");
        Verifier.verifyUserIdentifyTopLevelField(record, "1");
        assertEquals("{}", (String) record.getUserIdentity().get("randomField1"));
        assertEquals("randomValue", (String) record.getUserIdentity().get("randomField2"));
        assertEquals("[\"value1\",\"value2\",\"value3\"]", (String) record.getUserIdentity().get("randomField3"));
        Verifier.verifyResources((List<Resource>) record.getResources(), 3);

        assertTrue(serializer.hasNextRecord());
        clientRecord = serializer.getNextRecord();
        record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, "2");
        Verifier.verifyResources((List<Resource>) record.getResources(), 3);
        serializer.close();
    }

    @Test
    public void testNewFieldNameAtRandomPlaces() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testNewFieldsAtRandomPlaces);
        String logFile = new String(LibraryUtils.toByteArray(testFileInputStream));
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFile);
        RecordSerializer serializer = new DefaultRecordSerializer(new CloudTrailLog(null, null), jsonParser);

        assertTrue(serializer.hasNextRecord());
        CloudTrailClientRecord clientRecord = serializer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughDataStore(record, "", "1");
        Verifier.verifyUserIdentifyTopLevelField(record, "1");
        Verifier.verifyResources((List<Resource>) record.getResources(), 1);
        assertEquals("{}", (String) record.getUserIdentity().get("randomField1"));
        assertEquals("randomValue", (String) record.get("randomField2"));
        assertTrue(((String) record.getRequestParameters()).contains("[\"value1\",\"value2\",\"value3\"]"));
        assertTrue(serializer.hasNextRecord());
        clientRecord = serializer.getNextRecord();
        record = clientRecord.getRecord();
        Verifier.verifyRecordTopLevelFieldThroughGetter(record, "2");
        Verifier.verifyResources((List<Resource>) record.getResources(), 1);
        serializer.close();
    }
}
