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
package com.amazonaws.services.cloudtrail.clientlibrary;

import com.amazonaws.services.cloudtrail.clientlibrary.configuration.AWSCloudTrailClientConfiguration;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.clientlibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.clientlibrary.manager.AWSCloudTrailClientS3Manager;
import com.amazonaws.services.cloudtrail.clientlibrary.manager.AWSCloudTrailClientSqsManager;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;


/**
 * Internal use only.
 *
 * This class creates AWSCloudTrailRecordReader objects. It encapsulates maintains instance of the
 * objects that AWSCloudTrailRecordReader will use to limit the parameters that we needed to
 * create an instance.
 */
public class AWSCloudTrailClientRecordReaderFactory {
    /**
     * In instance of AWSCloudTrailClientConfiguration.
     */
    private AWSCloudTrailClientConfiguration config;

    /**
     * User's implementation of RecordsProcessor.
     */
    private RecordsProcessor recordsProcessor;

    /**
     * User's implementation of SourceFilter.
     */
    private SourceFilter sourceFilter;

    /**
     * User's implementation of RecordFilter.
     */
    private RecordFilter recordFilter;

    /**
     * User's implementation of ProgressReporter.
     */
    private ProgressReporter progressReporter;

    /**
     * User's implementation of ExceptionHandler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The class responsible for SQS related operation.
     */
    private AWSCloudTrailClientSqsManager sqsManager;

    /**
     * The class responsible for S3 related operation.
     */
    private AWSCloudTrailClientS3Manager s3Manager;

    /**
     * AWSCloudTrailRecordReaderFactory constructor, except AWSCloudTrailClientConfiguration
     * other parameters can be null.
     *
     * @param builder
     */
    private AWSCloudTrailClientRecordReaderFactory(Builder builder) {
        this.config = builder.config;
        this.recordsProcessor = builder.recordsProcessor;
        this.sourceFilter = builder.sourceFilter;
        this.recordFilter = builder.recordFilter;
        this.progressReporter = builder.progressReporter;
        this.exceptionHandler = builder.exceptionHandler;
        this.sqsManager = builder.sqsManager;
        this.s3Manager = builder.s3Manager;

        this.validate();
    }

    public static class Builder {
        private final AWSCloudTrailClientConfiguration config;
        private RecordsProcessor recordsProcessor;
        private SourceFilter sourceFilter;
        private RecordFilter recordFilter;
        private ProgressReporter progressReporter;
        private ExceptionHandler exceptionHandler;
        private AWSCloudTrailClientS3Manager s3Manager;
        private AWSCloudTrailClientSqsManager sqsManager;

        public Builder(AWSCloudTrailClientConfiguration config) {
            this.config = config;
        }

        public Builder withRecordsProcessor(RecordsProcessor recordsProcessor) {
            this.recordsProcessor = recordsProcessor;
            return this;
        }

        public Builder withSourceFilter(SourceFilter sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        public Builder withRecordFilter(RecordFilter recordFilter) {
            this.recordFilter = recordFilter;
            return this;
        }

        public Builder withProgressReporter(ProgressReporter progressReporter) {
            this.progressReporter = progressReporter;
            return this;
        }

        public Builder withExceptionHandler(ExceptionHandler exceptionHander) {
            this.exceptionHandler = exceptionHander;
            return this;
        }

        public Builder withS3Manager(AWSCloudTrailClientS3Manager s3Manager) {
            this.s3Manager = s3Manager;
            return this;
        }

        public Builder withSQSManager(AWSCloudTrailClientSqsManager sqsManager) {
            this.sqsManager = sqsManager;
            return this;
        }

        public AWSCloudTrailClientRecordReaderFactory build() {
            return new AWSCloudTrailClientRecordReaderFactory(this);
        }
    }

    /**
     * Create an instance of AWSCloudTrailRecordReader object.
     * @param metrics
     * @return
     */
    public AWSCloudTrailClientRecordReader createReader() {
        AWSCloudTrailClientRecordReader reader = new AWSCloudTrailClientRecordReader(
                this.recordsProcessor, this.sourceFilter, this.recordFilter, this.progressReporter, this.exceptionHandler,
                this.sqsManager, this.s3Manager, this.config);
        return reader;
    }

    /**
     * Convenient function to validate input
     */
    private void validate() {
        ClientLibraryUtils.checkArgumentNotNull(this.config, "configuration is null");
        ClientLibraryUtils.checkArgumentNotNull(this.recordsProcessor, "recordsProcessor is null");
        ClientLibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null");
        ClientLibraryUtils.checkArgumentNotNull(this.recordFilter, "recordFilter is null");
        ClientLibraryUtils.checkArgumentNotNull(this.progressReporter, "progressReporter is null");
        ClientLibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exceptionHander is null");
        ClientLibraryUtils.checkArgumentNotNull(this.s3Manager, "s3Manager is null");
        ClientLibraryUtils.checkArgumentNotNull(this.sqsManager, "sqsManager is null");
    }
}
