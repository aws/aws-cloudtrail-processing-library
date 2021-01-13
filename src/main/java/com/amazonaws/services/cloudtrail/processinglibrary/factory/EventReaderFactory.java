/*******************************************************************************
 * Copyright 2010-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * This class creates {@link EventReader} objects. It encapsulates and maintains instances of the objects that
 * <code>EventReader</code> will use to limit the parameters needed to create an instance.
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
     * </p>
     * @param builder a {@link Builder} object to use to create the <code>EventReaderFactory</code>.
     */
    private EventReaderFactory(Builder builder) {
        config = builder.config;
        eventsProcessor = builder.eventsProcessor;
        sourceFilter = builder.sourceFilter;
        eventFilter = builder.eventFilter;
        progressReporter = builder.progressReporter;
        exceptionHandler = builder.exceptionHandler;
        sqsManager = builder.sqsManager;
        s3Manager = builder.s3Manager;

        validate();
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
     * @return the {@link EventReader}.
     */
    public EventReader createReader() {
        return new EventReader(eventsProcessor, sourceFilter, eventFilter, progressReporter, exceptionHandler, sqsManager, s3Manager, config);
    }

    /**
     * Validate input parameters.
     */
    private void validate() {
        LibraryUtils.checkArgumentNotNull(config, "Configuration is null.");
        LibraryUtils.checkArgumentNotNull(eventsProcessor, "Events Processor is null.");
        LibraryUtils.checkArgumentNotNull(sourceFilter, "Source Filter is null.");
        LibraryUtils.checkArgumentNotNull(eventFilter, "Event Filter is null.");
        LibraryUtils.checkArgumentNotNull(progressReporter, "Progress Reporter is null.");
        LibraryUtils.checkArgumentNotNull(exceptionHandler, "Exception Handler is null.");
        LibraryUtils.checkArgumentNotNull(s3Manager, "S3 Manager is null.");
        LibraryUtils.checkArgumentNotNull(sqsManager, "SQS Manager is null.");
    }
}
