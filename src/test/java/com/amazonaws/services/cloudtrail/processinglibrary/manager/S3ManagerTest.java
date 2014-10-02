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
package com.amazonaws.services.cloudtrail.processinglibrary.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.apache.http.client.methods.HttpRequestBase;
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
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.progress.ProgressStatus;
import com.amazonaws.services.cloudtrail.processinglibrary.serializer.RecordSerializerTest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3ManagerTest {
    private S3Manager s3Manager;

    //test file SerializerTestFileCompact.json.gz has 6 records
    private static final String testFilePath = "/resources/SerializerTestFileCompact.json.gz";

    @Mock
    private AmazonS3Client s3Client;

    @Mock
    private ProcessingConfiguration config;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private ProgressReporter progressReporter;

    @Mock
    private S3Object s3Object;

    @Mock
    private ObjectMetadata s3ObjectMetaData;

    @Mock
    private HttpRequestBase httpRequestBase;

    @Mock
    private CloudTrailLog log;

    @Mock
    private CloudTrailSource source;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(this.config.getNRecordsPerEmit()).thenReturn(5);
        when(this.config.getAwsCredentialsProvider()).thenReturn(null);
        when(this.config.getS3Region()).thenReturn("us-east-1");
        when(this.log.getS3Bucket()).thenReturn("mybucket");
        when(this.log.getS3ObjectKey()).thenReturn("myKey");

        InputStream inputStream = RecordSerializerTest.class.getResourceAsStream(testFilePath);
        S3ObjectInputStream s3InputStream = new S3ObjectInputStream(inputStream, httpRequestBase);
        when(s3Object.getObjectContent()).thenReturn(s3InputStream);

        s3Manager = new S3Manager(this.s3Client, this.config, this.exceptionHandler, this.progressReporter);
    }

    @Test
    public void testDownloadLogSuccess() {
        when(this.s3Client.getObject(anyString(), anyString())).thenReturn(s3Object);
        when(this.s3Object.getObjectMetadata()).thenReturn(s3ObjectMetaData);
        when(this.s3ObjectMetaData.getContentLength()).thenReturn(100L);
        s3Manager.downloadLog(log, source);

        verify(this.exceptionHandler, times(0)).handleException(any(ProcessingLibraryException.class));

        ArgumentCaptor<ProgressStatus> statusArgument = ArgumentCaptor.forClass(ProgressStatus.class);
        verify(progressReporter, times(1)).reportEnd(statusArgument.capture(), any(Object.class));
        assertTrue(statusArgument.getValue().getStatusInfo().isSuccess());
    }

    /**
     * Test AmazonServiceException thrown when download log file will be caught
     * and ExceptionHandler will be called to handle the exception. SQS message
     * won't be deleted.
     */
    @Test
    public void testDownloadLogWithDownloadS3FilesThrowAmazonServiceException() {
        doThrow(new AmazonServiceException(null)).when(this.s3Client).getObject(anyString(), anyString());

        s3Manager.downloadLog(log, source);

        ArgumentCaptor<ProcessingLibraryException> argument = ArgumentCaptor.forClass(ProcessingLibraryException.class);
        verify(this.exceptionHandler, times(1)).handleException(argument.capture());
        assertTrue(argument.getValue() instanceof ProcessingLibraryException);

        ArgumentCaptor<ProgressStatus> statusArgument = ArgumentCaptor.forClass(ProgressStatus.class);
        verify(progressReporter, times(1)).reportEnd(statusArgument.capture(), any(Object.class));
        assertFalse(statusArgument.getValue().getStatusInfo().isSuccess());
    }

    /**
     * Test RuntimeException thrown when download log file will propagate to the
     * top level, and ExceptionHandler. SQS message won't be deleted.
     */
    @Test
    public void testDownloadLogWithDownloadS3FilesThrowRuntimeException() {
        doThrow(new IllegalStateException()).when(this.s3Client).getObject(anyString(), anyString());

        try {
            s3Manager.downloadLog(log, source);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
        verify(this.exceptionHandler, times(0)).handleException(any(ProcessingLibraryException.class));

        ArgumentCaptor<ProgressStatus> statusArgument = ArgumentCaptor.forClass(ProgressStatus.class);
        verify(progressReporter, times(1)).reportEnd(statusArgument.capture(), any(Object.class));
        assertFalse(statusArgument.getValue().getStatusInfo().isSuccess());
    }
}
