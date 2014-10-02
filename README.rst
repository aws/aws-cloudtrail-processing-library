.. |library| replace:: AWS CloudTrail Processing Library
.. |ct| replace:: AWS CloudTrail

AWS CloudTrail Processing Library
=================================

The |library| helps Java developers to easily consume and process log files from |ct|.

Requirements
------------

- **AWS Java SDK 1.8**: In order to use the |library|, you'll also need the `AWS Java SDK`__.
- **Java 1.7**: The |library| requires `Java 1.7 (Java SE 7)`__ or later.

.. __: https://github.com/aws/aws-sdk-java
.. __: http://www.oracle.com/technetwork/java/javase/overview/index.html

Overview
--------

The |library| is an open source client side library that helps Java developer to easily consume and process log files
from |ct|.

|ct| delivers near real-time user activities insights, but the lack of open source library that consume and process
those logs in any compelling way is limited. The |library| provides customers who do not use third party log management and
analytics service such as Sumo Logic, boundary a nice way to consume and process log files from AWS CloudTrail by only
write few lines of code.

|library| is not part of AWS SDK neither CLI. Instead It is a client side library that hosted in Github. Customers can
either down source code from Github or pull Jar file through Maven Central Repository, either way provide customers a
convenient way to integrate the |library| with their application. The |library| has very few open source dependencies,
such as AWS Java SDK, Jackson and BouncyCastle etc. All of them are under Apache 2.0 license.

Configuration
~~~~~~~~~~~~~

The configuration is loaded from class path.

- **accessKey**: Specify your AWS access key.
- **secretKey**: Specify your AWS secrete key.
- **sqsUrl**: The sqs url where you wish to pull CloudTrail notification from.
- **sqsRegion**: The SQS end point specific to a region.
- **visibilityTimeout**: # A period of time during which Amazon SQS prevents other consuming components from receiving
  and processing that message.
- **s3Region**: The S3 region where all log files come from. Don't configure this parameter if you have logs come
  multiple regions. The default one will handle multiple regions case; however doing so may result a higher latency.
- **threadCount**: Number of threads to download S3 files in parallel when you enable thread mode.
- **threadTerminationDelay**: The duration in seconds to wait for thread pool termination before issue shutDownNow.
- **nRecordsPerEmit**: The max number of CloudTrailClientRecord that buffered before emit, AWS CloudTrail Processing
  Library may emit less than this number and will skip emit 0 record.
- **enableRawRecordInfo**: Whether to include raw record in CloudTrailDeliveryInfo.

Building from Source
--------------------

After you've downloaded the code from GitHub, you can build it using Maven. To disable GPG signing in the build, use
this command::

 mvn clean install -Dgpg.skip=true