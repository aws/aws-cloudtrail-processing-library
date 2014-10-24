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

package com.amazonaws.services.cloudtrail.processinglibrary.reader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.ProcessingLibraryException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailClientRecord;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressState;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializerTest;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.model.Message;

public class RecordReaderTest {
    private RecordReader recordReader;

    //test file SerializerTestFileCompact.json.gz has 6 records
    private static final String testFilePath = "/resources/SerializerTestFileCompact.json.gz";

    @Mock
    private RecordsProcessor recordsProcessor;

    @Mock
    private SourceFilter sourceFilter;

    @Mock
    private RecordFilter recordFilter;

    @Mock
    private ProgressReporter progressReporter;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private SqsManager sqsManager;

    @Mock
    private S3Manager s3Manager;

    @Mock
    private S3Object s3Object;

    @Mock
    private ProcessingConfiguration configuration;

    @Mock
    private HttpRequestBase httpRequestBase;

    @Mock
    private CloudTrailLog log1, log2;

    @Mock
    private SQSBasedSource source1, source2, source3;

    @Mock
    private Message message1, message2, message3;

    @Mock
    private List<Message> sqsMessages;

    @Captor
    ArgumentCaptor<List<CloudTrailClientRecord>> clientRecordsArgument;

    @Before
    public void setup() throws Exception{
        MockitoAnnotations.initMocks(this);

        when(log1.getS3Bucket()).thenReturn("bucket1");
        when(log1.getS3Bucket()).thenReturn("objectKey1");

        List<CloudTrailLog> logs = new ArrayList<CloudTrailLog>();
        logs.add(log1);
        when(source1.getLogs()).thenReturn(logs);

        List<CloudTrailSource> sources = new ArrayList<CloudTrailSource>();

        sources.add(source1);
        sources.add(source2);
        sources.add(source3);

        when(source1.getSqsMessage()).thenReturn(message1);
        when(source2.getSqsMessage()).thenReturn(message2);
        when(source3.getSqsMessage()).thenReturn(message3);

        Map<String, String> attributes1 = new HashMap<String, String>();
        Map<String, String> attributes2 = new HashMap<String, String>();
        Map<String, String> attributes3 = new HashMap<String, String>();

        attributes1.put("accountId", "account1");
        attributes2.put("accountId", "account2");
        attributes3.put("accountId", "account3");

        when(sqsManager.pollQueue()).thenReturn(sqsMessages);
        when(sqsManager.parseMessage(sqsMessages)).thenReturn(sources);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(10);

        InputStream inputStream = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream = new S3ObjectInputStream(inputStream, httpRequestBase);
        byte[] logFileBytes = LibraryUtils.toByteArray(s3InputStream);
        when(s3Manager.downloadLog(any(CloudTrailLog.class), any(CloudTrailSource.class))).thenReturn(logFileBytes);

        recordReader = new RecordReader(recordsProcessor, sourceFilter, recordFilter, progressReporter, exceptionHandler, sqsManager, s3Manager, configuration);
    }

    /**
     * Test some sources are filtered.
     * @throws CallbackException
     */
    @Test
    public void testFilteredOutSourceIsDeleted() throws CallbackException {
        when(sourceFilter.filterSource(source1)).thenReturn(true);
        recordReader.processSource(source1);
        verify(sqsManager, times(1)).deleteMessageFromQueue(source1, ProgressState.deleteMessage);

    }

    /**
     * Test all source are passed
     * @throws CallbackException
     */
    @Test
    public void testFilteredInSourceIsDeleted() throws CallbackException {
        when(sourceFilter.filterSource(source1)).thenReturn(false);
        recordReader.processSource(source1);
        verify(sqsManager, times(1)).deleteMessageFromQueue(source1, ProgressState.deleteFilteredMessage);

    }

    @Test
    public void testProcessRecordsCallRecordsProcessorOnce() throws CallbackException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(true);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(10);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(1)).process(clientRecordsArgument.capture());
        assertEquals(6, clientRecordsArgument.getValue().size());

        verify(this.sqsManager, times(1)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }

    @Test
    public void testProcessRecordsCallRecordsProcessorMultipleTimes() throws ProcessingLibraryException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(true);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(4);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(2)).process(clientRecordsArgument.capture());
        assertEquals(4, clientRecordsArgument.getAllValues().get(0).size());
        assertEquals(2, clientRecordsArgument.getAllValues().get(1).size());

        verify(this.sqsManager, times(1)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }

    @Test
    public void testProcessRecordsNoPassRecordFilter() throws CallbackException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(false);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(5);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(0)).process(anyListOf(CloudTrailClientRecord.class));
        verify(this.sqsManager, times(1)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }

    @Test
    public void testProcessRecordsOnlyFirstNoPassRecordFilter() throws CallbackException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(false).thenReturn(true);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(5);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(1)).process(clientRecordsArgument.capture());
        assertEquals(5, clientRecordsArgument.getValue().size());
        verify(this.sqsManager, times(1)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }



    /**
     * Test RuntimeException thrown when calling user's recordFilter function. In this case, RuntimeException will
     * propagate to top level and ExceptionHandler will not be called. SQS message won't be deleted.
     */
    @Test(expected = RuntimeException.class)
    public void testProcessRecordsWithRecordFilterThrowRunTimeException() throws CallbackException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);
        doThrow(new RuntimeException()).when(this.recordFilter).filterRecord(any(CloudTrailClientRecord.class));

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(5);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(0)).process(anyListOf(CloudTrailClientRecord.class));
        verify(this.exceptionHandler, times(0)).handleException(any(ProcessingLibraryException.class));
        verify(this.sqsManager, times(0)).deleteMessageFromQueue(any(CloudTrailSource.class), eq(ProgressState.deleteMessage));
    }

    /**
     * Test RuntimeException thrown when calling user's recordsProcessor function. In this case, RuntimeException will
     * propagate to top level and ExceptionHandler will not be called. SQS message won't be deleted.
     */
    @Test(expected = RuntimeException.class)
    public void testProcessRecordsWithRecordsProcessorThrowRunTimeException() throws CallbackException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(true);
        doThrow(new RuntimeException()).when(this.recordsProcessor).process(anyListOf(CloudTrailClientRecord.class));

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(5);

        recordReader.processSource(source1);

        verify(this.recordFilter, times(5)).filterRecord(any(CloudTrailClientRecord.class));
        verify(this.recordsProcessor, times(1)).process(anyListOf(CloudTrailClientRecord.class));
        verify(this.exceptionHandler, times(0)).handleException(any(ProcessingLibraryException.class));
        verify(this.sqsManager, times(0)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }

    /**
     * Test a source has multiple logs.
     */
    @Test
    public void testProcessRecordsMultipleLogs() throws CallbackException {
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);

        when(log1.getS3Bucket()).thenReturn("bucket1");
        when(log1.getS3Bucket()).thenReturn("objectKey1");
        when(log2.getS3Bucket()).thenReturn("bucket2");
        when(log2.getS3Bucket()).thenReturn("objectKey2");

        List<CloudTrailLog> logs = new ArrayList<CloudTrailLog>();
        logs.add(log1);
        logs.add(log2);

        when(source1.getLogs()).thenReturn(logs);

        InputStream inputStream = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream = new S3ObjectInputStream(inputStream, httpRequestBase);

        InputStream inputStream2 = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream2 = new S3ObjectInputStream(inputStream2, httpRequestBase);

        when(s3Manager.getObject(anyString(), anyString())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3InputStream).thenReturn(s3InputStream2);

        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(true);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(5);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(4)).process(clientRecordsArgument.capture());
        assertEquals(5, clientRecordsArgument.getAllValues().get(0).size());
        assertEquals(1, clientRecordsArgument.getAllValues().get(1).size());
        assertEquals(5, clientRecordsArgument.getAllValues().get(2).size());
        assertEquals(1, clientRecordsArgument.getAllValues().get(3).size());

        verify(this.sqsManager, times(1)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }

    /**
     * Test a source has multiple logs. Filter record from the first log fail due to IOException
     * and process the second log success. Message is not deleted.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProcessRecordMultipleLogsFirstOneFailAtFilterWithIOException() throws CallbackException{
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);

        when(log1.getS3Bucket()).thenReturn("bucket1");
        when(log1.getS3Bucket()).thenReturn("objectKey1");
        when(log2.getS3Bucket()).thenReturn("bucket2");
        when(log2.getS3Bucket()).thenReturn("objectKey2");

        List<CloudTrailLog> logs = new ArrayList<CloudTrailLog>();
        logs.add(log1);
        logs.add(log2);

        when(source1.getLogs()).thenReturn(logs);

        InputStream inputStream = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream = new S3ObjectInputStream(inputStream, httpRequestBase);

        InputStream inputStream2 = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream2 = new S3ObjectInputStream(inputStream2, httpRequestBase);

        when(s3Manager.getObject(anyString(), anyString())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3InputStream).thenReturn(s3InputStream2);

        //first filter has exception while second one success
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenThrow(IOException.class).thenReturn(true);

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(10);

        recordReader.processSource(source1);

        verify(this.recordsProcessor, times(1)).process(clientRecordsArgument.capture());
        assertEquals(6, clientRecordsArgument.getAllValues().get(0).size());

        verify(this.sqsManager, times(0)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }

    /**
     * Test a source has multiple logs. Filter record from the first log fail due to RuntimeException
     * and process the second log success. Message is not deleted.
     */
    @Test
    public void testProcessRecordMultipleLogsFirstOneFailAtProcessorWithRuntimeException() throws CallbackException{
        when(this.sourceFilter.filterSource(source1)).thenReturn(true);

        when(log1.getS3Bucket()).thenReturn("bucket1");
        when(log1.getS3Bucket()).thenReturn("objectKey1");
        when(log2.getS3Bucket()).thenReturn("bucket2");
        when(log2.getS3Bucket()).thenReturn("objectKey2");

        List<CloudTrailLog> logs = new ArrayList<CloudTrailLog>();
        logs.add(log1);
        logs.add(log2);

        when(source1.getLogs()).thenReturn(logs);

        InputStream inputStream = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream = new S3ObjectInputStream(inputStream, httpRequestBase);

        InputStream inputStream2 = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream2 = new S3ObjectInputStream(inputStream2, httpRequestBase);

        when(s3Manager.getObject(anyString(), anyString())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3InputStream).thenReturn(s3InputStream2);
        when(this.recordFilter.filterRecord(any(CloudTrailClientRecord.class))).thenReturn(true);

        //first processor has exception while second one success
        doThrow(RuntimeException.class).doNothing().when(this.recordsProcessor).process(anyListOf(CloudTrailClientRecord.class));

        when(this.configuration.getMaxRecordsPerEmit()).thenReturn(10);

        try {
            recordReader.processSource(source1);
        } catch (RuntimeException e) {

        }
        verify(this.recordsProcessor, times(1)).process(clientRecordsArgument.capture());
        assertEquals(6, clientRecordsArgument.getAllValues().get(0).size());
        verify(this.sqsManager, times(0)).deleteMessageFromQueue(any(CloudTrailSource.class), any(ProgressState.class));
    }
}
