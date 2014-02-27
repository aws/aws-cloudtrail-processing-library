# AWS CloudTrail Client Library

The **AWS CloudTrail Client** helps Java developers to easily consume and process log files from AWS CloudTrail.

## Requirements
 + **AWS Java SDK 1.71**: In order to use the AWS CloudTrail Client Library, you'll also need the [AWS Java SDK](https://github.com/aws/aws-sdk-java). 
 + **Java 1.7**: The AWS CloudTrail Client Library requires [Java 1.7 (Java SE 7)](http://www.oracle.com/technetwork/java/javase/overview/index.html) or later.
 + **bcprov-jdk15on 1.50**: You will need bouncycastle to verify AWS CloudTrail log file signature.
 + **bcpkix-jdk15on 1.50**: You will need bouncycastle to verify AWS CloudTrail log file signature.

## Overview

## Implementation Highlights

## Configuration
 + **accessKey**: Specify your AWS access key.
 + **secretKey**: Specify your AWS secrete key.
 + **sqsUrl**: The sqs url where you wish to pull CloudTrail notification from.
 + **sqsEndPoint**: The SQS end point specific to a region.
 + **sqsMessageSizeLimit**: Number of SQS messages polled per API call.
 + **visibilityTimeout**: # A period of time during which Amazon SQS prevents other consuming components from receiving and processing that message.
 + **waitTimeSeconds**: Enabled long poll by seting a non-zero value to the WaitTimeSeconds parameter.
 + **s3EndPoint**: The S3 end point specific to a region.
 + **threadCount**: Number of threads to download S3 files in parallel when you enable thread mode.
 + **threadTerminationDelay**: The duration in seconds to wait for thread pool termination before issue shutDownNow.
 + **threadPoolFixedDelay**: The duration in seconds to sleep for each iteration of while loop.
 + **threadPoolQueueCoreSize**: The core size of a shared queue in thread pool.
 + **threadPoolQueueMaxSize**: The max size of a shared queue in thread pool.
 + **recordBufferSize**: The max number of AWSCloudTrailClientRecord emit a time.

## Building from Source
After you've downloaded the code from GitHub, you can build it using Maven. To disable GPG signing in the build, use this command: `mvn clean install -Dgpg.skip=true`
