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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RawLogDeliveryRecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecordSerializerGetLocationTest {
    private static final String testFilePath = "/resources/SerializerTestFileLocation.json";

    @Test
    public void testSerializer() throws IOException {
        InputStream testFileInputStream = RecordSerializerGetLocationTest.class.getResourceAsStream(testFilePath);
        String logFileContent = new String(LibraryUtils.toByteArray(testFileInputStream));

        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = mapper.getFactory().createParser(logFileContent);
        RecordSerializer serializer = new RawLogDeliveryRecordSerializer(logFileContent, new CloudTrailLog(null, null), jsonParser);

        this.testRootUser(serializer, "1");
        this.testIAMUser(serializer, "2");
        this.testRootInSelfSession(serializer, "3");
        this.testIAMUserInSelfSession(serializer, "4");
        this.testFASSession(serializer, "5");
        this.testWebIdentityFederatedRoleSession(serializer, "6");
        this.testNoMoreRecords(serializer);
    }

    public void testRootUser(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 12, 691);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    public void testIAMUser(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 699, 1463);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    public void testRootInSelfSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 1465, 2151);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    public void testIAMUserInSelfSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 2161, 3012);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testFASSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 3019, 3801);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testWebIdentityFederatedRoleSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 3803, 4785);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testNoMoreRecords(RecordSerializer serizlizer) throws IOException {
        assertFalse(serizlizer.hasNextRecord());
    }
}
