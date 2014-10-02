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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailDeliveryInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.LogDeliveryInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.CloudTrailRecordField;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.Resource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.SessionIssuer;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.WebIdentitySessionContext;

public class Verifier {
    private static Date matchDate;

    static {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            matchDate = isoFormat.parse("2013-11-01T00:00:00Z");
        } catch (ParseException e) {

        }
    }

    public static void verifyRecordTopLevelFieldThroughGetter(CloudTrailRecord record, String suffix) {
        assertEquals("1." + suffix, record.getEventVersion());
        assertEquals(matchDate, record.getEventTime());
        assertEquals("eventSource" + suffix, record.getEventSource());
        assertEquals("eventName" + suffix, record.getEventName());
        assertEquals("awsRegion" + suffix, record.getAwsRegion());
        assertEquals("sourceIPAddress" + suffix, record.getSourceIPAddress());
        assertEquals("userAgent" + suffix, record.getUserAgent());
        assertEquals("errorCode" + suffix, record.getErrorCode());
        assertEquals("errorMessage" + suffix, record.getErrorMessage());
        assertEquals("userIdentityAccountId" + suffix, record.getAccountId());
        assertEquals("AwsApiCall", record.getEventType());
        assertEquals("apiVersion" + suffix, record.getApiVersion());
        assertEquals("recipientAccountId" + suffix, record.getRecipientAccountId());
    }

    public static void verifyRecordTopLevelFieldNull(CloudTrailRecord record) {
        assertNull(record.getEventTime());
        assertNull(record.getEventSource());
        assertNull(record.getEventName());
        assertNull(record.getAwsRegion());
        assertNull(record.getSourceIPAddress());
        assertNull(record.getUserAgent());
        assertNull(record.getErrorCode());
        assertNull(record.getErrorMessage());
        assertNull(record.getAccountId());
        assertNull(record.getResources());
        assertNull(record.getEventType());
        assertNull(record.getApiVersion());
        assertNull(record.getRecipientAccountId());
    }

    /**
     *
     * @param record The record to be verified
     * @param valueSuffix The value suffix
     * @param keySuffix The key suffix
     */
    public static void verifyRecordTopLevelFieldThroughDataStore(CloudTrailRecord record, String keySuffix, String valueSuffix) {
        assertEquals("1." + valueSuffix, record.get(CloudTrailRecordField.eventVersion.name() + keySuffix));
        assertEquals("eventSource" + valueSuffix, record.get(CloudTrailRecordField.eventSource.name() + keySuffix));
        assertEquals("eventName" + valueSuffix, record.get(CloudTrailRecordField.eventName.name() + keySuffix));
        assertEquals("awsRegion" + valueSuffix, record.get(CloudTrailRecordField.awsRegion.name() + keySuffix));
        assertEquals("sourceIPAddress" + valueSuffix, record.get(CloudTrailRecordField.sourceIPAddress.name() + keySuffix));
        assertEquals("userAgent" + valueSuffix, record.get(CloudTrailRecordField.userAgent.name() + keySuffix));
        assertEquals("errorCode" + valueSuffix, record.get(CloudTrailRecordField.errorCode.name() + keySuffix));
        assertEquals("errorMessage" + valueSuffix, record.get(CloudTrailRecordField.errorMessage.name() + keySuffix));
        assertEquals("AwsApiCall", record.get(CloudTrailRecordField.eventType.name() + keySuffix));
        assertEquals("apiVersion" + valueSuffix, record.get(CloudTrailRecordField.apiVersion.name() + keySuffix));
        assertEquals("recipientAccountId" + valueSuffix, record.get(CloudTrailRecordField.recipientAccountId.name() + keySuffix));
    }

    public static void verifyUserIdentifyTopLevelField(CloudTrailRecord record, String suffix) {
        verifyUserIdentifyTopLevelField(record, suffix, false);
    }

    public static void verifyUserIdentifyTopLevelField(CloudTrailRecord record, String suffix, boolean userNameNull) {
        assertEquals("userIdentityType" + suffix, record.getUserIdentity().getIdentityType());
        assertEquals("userIdentityPrincipalId" + suffix, record.getUserIdentity().getPrincipalId());
        assertEquals("userIdentityArn" + suffix, record.getUserIdentity().getARN());
        assertEquals("userIdentityAccountId" + suffix, record.getUserIdentity().getAccountId());
        assertEquals("userIdentityAccessKeyId" + suffix, record.getUserIdentity().getAccessKeyId());
        if (userNameNull) {
            assertTrue(record.getUserIdentity().getUserName() == null);
        } else {
            assertEquals("userIdentityUserName" + suffix, record.getUserIdentity().getUserName());
        }
    }

    public static void verifySessionIssuer(SessionIssuer sessionIssuer, String suffix) {
        assertEquals("sessionIssuerType" + suffix, sessionIssuer.getType());
        assertEquals("sessionIssuerPrincipalId" + suffix, sessionIssuer.getPrincipalId());
        assertEquals("sessionIssuerArn" + suffix, sessionIssuer.getArn());
        assertEquals("sessionIssuerAccountId" + suffix, sessionIssuer.getAccountId());
        assertEquals("sessionIssuerUserName" + suffix, sessionIssuer.getUserName());
    }

    public static void verifyAttributes(Map<String, String> attributes) {
        assertEquals("value1", attributes.get("key1"));
        assertEquals("value2", attributes.get("key2"));
    }

    public static void verifyInvokedBy(String invokedBy, String suffix) {
        assertEquals("invokedBy" + suffix, invokedBy);
    }

    public static void verifyWIF(WebIdentitySessionContext webIdFederationData, String suffix) {
        assertEquals("webIdFederationDataFederatedProvider" + suffix, webIdFederationData.getFederatedProvider());
    }

    public static void verifyDeliveryInfoCharOffset(CloudTrailDeliveryInfo deliveryInfo, long start, long end) {
        assertEquals(start, ((LogDeliveryInfo)deliveryInfo).getCharStart());
        assertEquals(end, ((LogDeliveryInfo)deliveryInfo).getCharEnd());
    }

    public static void verifyResources(List<Resource> resources, int resourceCount) {
        assertEquals(resources.size(), resourceCount);
        for (int i = 0; i < resourceCount; i++) {
            assertEquals("acccountId" + (i+1), resources.get(i).getAccountId());
            assertEquals("arn:aws:iam:sample" + (i+1), resources.get(i).getArn());
        }
    }

    public static void verifyNull(CloudTrailRecord record) {
        assertNull(record.getAccountId());
        assertNull(record.getAdditionalEventData());
        assertNull(record.getAwsRegion());
        assertNull(record.getErrorCode());
        assertNull(record.getErrorMessage());
        assertNull(record.getEventId());
        assertNull(record.getEventName());
        assertNull(record.getEventSource());
        assertNull(record.getEventTime());
        assertNull(record.getEventVersion());
        assertNull(record.getRequestId());
        assertNull(record.getRequestParameters());
        assertNull(record.getResources());
        assertNull(record.getResponseElements());
        assertNull(record.getSourceIPAddress());
        assertNull(record.getUserAgent());
        assertNull(record.getUserIdentity());
    }

    public static void verifyRawRecordNull(CloudTrailClientRecord clientRecord) {
        assertNull(((LogDeliveryInfo)clientRecord.getDeliveryInfo()).getRawRecord());
    }

    public static void verifyRawRecordNotNull(CloudTrailClientRecord clientRecord) {
        String rawRecord = ((LogDeliveryInfo) clientRecord.getDeliveryInfo()).getRawRecord();
        assertNotNull(rawRecord);
        assertTrue(rawRecord.startsWith("{"));
        assertTrue(rawRecord.endsWith("}"));
    }
}
