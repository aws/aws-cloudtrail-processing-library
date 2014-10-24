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
 *******************************************************************************/

package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RawLogDeliveryRecordSerializer;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecordSerializerTest {
    private static final String testFilePath = "/resources/SerializerTestFileCompact.json";

    @Test
    public void testSerializer() throws IOException {
        InputStream testFileInputStream = RecordSerializerTest.class.getResourceAsStream(testFilePath);
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
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);

        Verifier.verifyUserIdentifyTopLevelField(record, suffix);

        assertEquals("{\"clusterIdentifier\":\"my-redshift-cluster\"}", record.getRequestParameters());
        assertEquals("{\"loggingEnabled\":false}", record.getResponseElements());

        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 12, 691);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    public void testIAMUser(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);

        Verifier.verifyUserIdentifyTopLevelField(record, suffix);

        assertEquals("{\"parameters\":[{\"isModifiable\":false,\"parameterValue\":\"2\",\"parameterName\":\"extra_float_digits\"}],\"parameterGroupName\":\"my-redshift-parameter-group\"}", record.getRequestParameters());
        assertEquals(null, record.getResponseElements());

        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 693, 1457);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    public void testRootInSelfSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);

        Verifier.verifyUserIdentifyTopLevelField(record, suffix);
        Verifier.verifyAttributes(record.getUserIdentity().getSessionContext().getAttributes());

        assertEquals(null, record.getRequestParameters());
        assertEquals(null, record.getResponseElements());

        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 1459, 2145);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    public void testIAMUserInSelfSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);

        assertEquals(null, record.getRequestParameters());
        assertEquals("{\"_return\":true}", record.getResponseElements());

        Verifier.verifyUserIdentifyTopLevelField(record, suffix, true);
        Verifier.verifyAttributes(record.getUserIdentity().getSessionContext().getAttributes());
        Verifier.verifySessionIssuer(record.getUserIdentity().getSessionContext().getSessionIssuer(), suffix);

        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 2147, 2998);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testFASSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);

        assertEquals("{\"resourcesSet\":{\"items\":[{\"resourceId\":\"ami-1a2b3c4d\"}]},\"tagSet\":{\"items\":[{\"key\":\"eagle\",\"value\":\"test\"}]}}", record.getRequestParameters());
        assertEquals(null, record.getResponseElements());

        Verifier.verifyUserIdentifyTopLevelField(record, suffix, true);
        Verifier.verifyAttributes(record.getUserIdentity().getSessionContext().getAttributes());
        Verifier.verifyInvokedBy(record.getUserIdentity().getInvokedBy(), suffix);

        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 3000, 3782);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testWebIdentityFederatedRoleSession(RecordSerializer serizlizer, String suffix) throws IOException {
        assertTrue(serizlizer.hasNextRecord());

        CloudTrailClientRecord clientRecord = serizlizer.getNextRecord();
        CloudTrailRecord record = clientRecord.getRecord();

        Verifier.verifyRecordTopLevelFieldThroughGetter(record, suffix);

        assertEquals("{\"regionSet\":{}}", record.getRequestParameters());
        assertEquals(null, record.getResponseElements());

        Verifier.verifyUserIdentifyTopLevelField(record, suffix, true);
        Verifier.verifyAttributes(record.getUserIdentity().getSessionContext().getAttributes());
        assertTrue(null == record.getUserIdentity().getInvokedBy());
        Verifier.verifyWIF(record.getUserIdentity().getSessionContext().getWebIdFederationData(), suffix);
        Verifier.verifyAttributes(record.getUserIdentity().getSessionContext().getWebIdFederationData().getAttributes());

        Verifier.verifyDeliveryInfoCharOffset(clientRecord.getDeliveryInfo(), 3784, 4766);
        Verifier.verifyRawRecordNotNull(clientRecord);
    }

    private void testNoMoreRecords(RecordSerializer serizlizer) throws IOException {
        assertFalse(serizlizer.hasNextRecord());
    }
}
