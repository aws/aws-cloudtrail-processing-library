/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.EventsProcessor;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.S3Manager;
import com.amazonaws.services.cloudtrail.processinglibrary.manager.SqsManager;
import com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;


/**
 * <i>Internal use only</i>.
 *
 * This class creates {@link com.amazonaws.services.cloudtrail.processinglibrary.reader.EventReader} objects. It
 * encapsulates and maintains instances of the objects that <code>EventReader</code> will use to limit the parameters needed
 * to create an instance.
 */
public class EventReaderFactory {

    private ProcessingConfiguration config;
    private EventsProcessor eventsProcessor;
    private SourceFilter sourceFilter;
    private EventFilter eventFilter;
    private ProgressReporter progressReporter;
    private ExceptionHandler exceptionHandler;

    /* The class responsible for SQS-related operations. */
    private SqsManager sqsManager;

    /* The class responsible for S3-related operations. */
    private S3Manager s3Manager;

    /**
     * EventReaderFactory constructor.
     * <p>
     * Except for ProcessingConfiguration, the other parameters can be <code>null</code>.
     *
     * @param builder a {@link Builder} object to use to create the <code>EventReaderFactory</code>.
     */
    private EventReaderFactory(Builder builder) {
        this.config = builder.config;
        this.eventsProcessor = builder.eventsProcessor;
        this.sourceFilter = builder.sourceFilter;
        this.eventFilter = builder.eventFilter;
        this.progressReporter = builder.progressReporter;
        this.exceptionHandler = builder.exceptionHandler;
        this.sqsManager = builder.sqsManager;
        this.s3Manager = builder.s3Manager;

        this.validate();
    }

    public static class Builder {
        private final ProcessingConfiguration config;
        private EventsProcessor eventsProcessor;
        private SourceFilter sourceFilter;
        private EventFilter eventFilter;
        private ProgressReporter progressReporter;
        private ExceptionHandler exceptionHandler;
        private S3Manager s3Manager;
        private SqsManager sqsManager;

        public Builder(ProcessingConfiguration config) {
            this.config = config;
        }

        public Builder withEventsProcessor(EventsProcessor eventsProcessor) {
            this.eventsProcessor = eventsProcessor;
            return this;
        }

        public Builder withSourceFilter(SourceFilter sourceFilter) {
            this.sourceFilter = sourceFilter;
            return this;
        }

        public Builder withEventFilter(EventFilter eventFilter) {
            this.eventFilter = eventFilter;
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

        public EventReaderFactory build() {
            return new EventReaderFactory(this);
        }
    }

    /**
     * Create an instance of an {@link EventReader}.
     *
     * @return the event reader.
     */
    public EventReader createReader() {
        EventReader reader = new EventReader(
                this.eventsProcessor, this.sourceFilter, this.eventFilter, this.progressReporter, this.exceptionHandler,
                this.sqsManager, this.s3Manager, this.config);
        return reader;
    }

    /**
     * Validate input parameters.
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(this.config, "configuration is null");
        LibraryUtils.checkArgumentNotNull(this.eventsProcessor, "eventsProcessor is null");
        LibraryUtils.checkArgumentNotNull(this.sourceFilter, "sourceFilter is null");
        LibraryUtils.checkArgumentNotNull(this.eventFilter, "eventFilter is null");
        LibraryUtils.checkArgumentNotNull(this.progressReporter, "progressReporter is null");
        LibraryUtils.checkArgumentNotNull(this.exceptionHandler, "exceptionHander is null");
        LibraryUtils.checkArgumentNotNull(this.s3Manager, "s3Manager is null");
        LibraryUtils.checkArgumentNotNull(this.sqsManager, "sqsManager is null");
    }
}
