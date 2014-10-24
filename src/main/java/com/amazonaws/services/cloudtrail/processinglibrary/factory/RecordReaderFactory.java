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

package com.amazonaws.services.cloudtrail.processinglibrary.factory;

import com.amazonaws.services.cloudtrail.processinglibrary.configuration.ProcessingConfiguration;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ExceptionHandler;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.ProgressReporter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.RecordsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.RecordReader;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;


/**
 * Internal use only.
 *
 * This class creates AWSCloudTrailRecordReader objects. It encapsulates maintains instance of the
 * objects that AWSCloudTrailRecordReader will use to limit the parameters that we needed to
 * create an instance.
 */
public class RecordReaderFactory {
    /**
     * In instance of AWSCloudTrailClientConfiguration.
     */
    private ProcessingConfiguration config;

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
    private SqsManager sqsManager;

    /**
     * The class responsible for S3 related operation.
     */
    private S3Manager s3Manager;

    /**
     * AWSCloudTrailRecordReaderFactory constructor, except AWSCloudTrailClientConfiguration
     * other parameters can be null.
     *
     * @param builder
     */
    private RecordReaderFactory(Builder builder) {
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
        private final ProcessingConfiguration config;
        private RecordsProcessor recordsProcessor;
        private SourceFilter sourceFilter;
        private RecordFilter recordFilter;
        private ProgressReporter progressReporter;
        private ExceptionHandler exceptionHandler;
        private S3Manager s3Manager;
        private SqsManager sqsManager;

        public Builder(ProcessingConfiguration config) {
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

        public Builder withS3Manager(S3Manager s3Manager) {
            this.s3Manager = s3Manager;
            return this;
        }

        public Builder withSQSManager(SqsManager sqsManager) {
            this.sqsManager = sqsManager;
            return this;
        }

        public RecordReaderFactory build() {
            return new RecordReaderFactory(this);
        }
    }

    /**
     * Create an instance of AWSCloudTrailRecordReader object.
     * @param metrics
     * @return
     */
    public RecordReader createReader() {
        RecordReader reader = new RecordReader(
                this.recordsProcessor, this.sourceFilter, this.recordFilter, this.progressReporter, this.exceptionHandler,
                this.sqsManager, this.s3Manager, this.config);
        return reader;
    }

    /**
     * Convenient function to validate input
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(this.config, "configuration is null");
        LibraryUtils.checkArgumentNotNull(this.recordsProcessor, "recordsProcessor is null");
        LibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null");
        LibraryUtils.checkArgumentNotNull(this.recordFilter, "recordFilter is null");
        LibraryUtils.checkArgumentNotNull(this.progressReporter, "progressReporter is null");
        LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exceptionHander is null");
        LibraryUtils.checkArgumentNotNull(this.s3Manager, "s3Manager is null");
        LibraryUtils.checkArgumentNotNull(this.sqsManager, "sqsManager is null");
    }
}
