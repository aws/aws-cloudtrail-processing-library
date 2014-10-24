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

package com.amazonaws.services.cloudtrail.processinglibrary.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicParseMessageInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.BasicPollQueueInfo;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializerTest;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class SqsManagerTest {
    private SqsManager sqsManager;

    private static final String TEST_FILE_PATH = "/resources/SQSMessageBody";
    private static final String BAD_TEST_PATH = "/resources/SQSMessageBodyWrongFormat";
    private static final String APPROXIMATE_RECEIVE_COUNT = "ApproximateReceiveCount";

    @Mock
    private AmazonSQSClient sqsClient;

    @Mock
    private ProcessingConfiguration config;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private ProgressReporter progressReporter;

    @Mock
    private ReceiveMessageResult receiveMessageResult;

    @Mock
    private Message message1, message2;

    List<Message> messages = new ArrayList<Message>();

    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(receiveMessageResult);
        when(receiveMessageResult.getMessages()).thenReturn(messages);

        Map<String, String> messageAttributes = new HashMap<String, String>();
        messageAttributes.put("ApproximateReceiveCount", "0");
        messageAttributes.put("accountId", "123456789012");

        InputStream sqsBodyInputStream = RecordSerializerTest.class.getResourceAsStream(TEST_FILE_PATH);
        byte[] logFileBytes = LibraryUtils.toByteArray(sqsBodyInputStream);
        String messageBody = new String(logFileBytes);

        when(message1.getReceiptHandle()).thenReturn("handle1");
        when(message2.getReceiptHandle()).thenReturn("handle2");
        when(message1.getAttributes()).thenReturn(messageAttributes);
        when(message2.getAttributes()).thenReturn(messageAttributes);
        when(message1.getBody()).thenReturn(messageBody);
        when(message2.getBody()).thenReturn(messageBody);

        messages.addAll(Arrays.asList(message1, message2));
        sqsManager = new SqsManager(sqsClient, config, exceptionHandler, progressReporter);
    }

    @Test
    public void testPollQueue() {
        List<CloudTrailSource> sources = sqsManager.parseMessage(messages);

        assertEquals(2, sources.size());

        SQSBasedSource source1 = (SQSBasedSource) sources.get(0);
        assertEquals("123456789012",  source1.getSourceAttributes().get("accountId"));
        assertEquals(0,  Integer.parseInt(source1.getSqsMessage().getAttributes().get(APPROXIMATE_RECEIVE_COUNT)));
        assertEquals("handle1",  source1.getSqsMessage().getReceiptHandle());
        assertEquals(2,  source1.getLogs().size());

        assertEquals(2,  source1.getLogs().size());
        CloudTrailLog log1 = source1.getLogs().get(0);
        assertEquals("my-test-bucket", log1.getS3Bucket());
        assertEquals("myprefix/AWSLogs/123456789012/CloudTrail/us-east-1/2014/03/09/123456789012_CloudTrail_us-east-1_20140309T2015Z_996mpJdKNLd1Yvzl.json.gz", log1.getS3ObjectKey());

        ArgumentCaptor<ProgressStatus> status = ArgumentCaptor.forClass(ProgressStatus.class);
        verify(this.progressReporter, times(2)).reportEnd(status.capture(), any(Object.class));
        assertEquals(ProgressState.parseMessage, status.getValue().getProgressState());
        assertTrue(status.getValue().getProgressInfo() instanceof BasicParseMessageInfo);
        assertTrue(status.getValue().getProgressInfo().isSuccess());
    }

    @Test
    public void testPollQueueAmazonException() {
        when(this.sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenThrow(new AmazonServiceException(null));

        sqsManager.pollQueue();
        ArgumentCaptor<ProgressStatus> status = ArgumentCaptor.forClass(ProgressStatus.class);
        verify(this.exceptionHandler, times(1)).handleException(any(ProcessingLibraryException.class));
        verify(this.progressReporter, times(1)).reportEnd(status.capture(), any(Object.class));

        assertEquals(ProgressState.pollQueue, status.getValue().getProgressState());
        assertTrue(status.getValue().getProgressInfo() instanceof BasicPollQueueInfo);
        assertFalse(status.getValue().getProgressInfo().isSuccess());
    }

    /**
     * Transform() will throw runtime exception, eventually will report fail.
     * @throws IOException
     */
    @Test
    public void testPollQueueWrongFormat() throws IOException {
        InputStream sqsBodyInputStream = RecordSerializerTest.class.getResourceAsStream(BAD_TEST_PATH);
        byte[] logFileBytes = LibraryUtils.toByteArray(sqsBodyInputStream);
        String messageBody = new String(logFileBytes);

        when(message1.getBody()).thenReturn(messageBody);
        when(message2.getBody()).thenReturn(messageBody);

        List<CloudTrailSource> sources = new ArrayList<CloudTrailSource>();
        try {
            sources = sqsManager.parseMessage(messages);
        } catch (Exception e) {
        }

        assertEquals(0, sources.size());
        verify(this.exceptionHandler, times(0)).handleException(any(ProcessingLibraryException.class));

        ArgumentCaptor<ProgressStatus> status = ArgumentCaptor.forClass(ProgressStatus.class);
        verify(this.progressReporter, times(1)).reportEnd(status.capture(), any(Object.class));

        assertEquals(ProgressState.parseMessage, status.getValue().getProgressState());
        assertTrue(status.getValue().getProgressInfo() instanceof BasicParseMessageInfo);
        assertFalse(status.getValue().getProgressInfo().isSuccess());
    }
}
