.. |library| replace:: AWS CloudTrail Processing Library
.. |ct| replace:: AWS CloudTrail
.. |sqs| replace:: Amazon SQS
.. |s3| replace:: Amazon S3


AWS CloudTrail Processing Library
=================================

The |library| is a Java client library that makes it easy to build an application that reads and processes
CloudTrail log files in a fault tolerant and highly scalable manner.

* `CloudTrail Product Page <http://aws.amazon.com/cloudtrail/>`_
* `CloudTrail Forum <https://forums.aws.amazon.com/forum.jspa?forumID=168/>`_
* `Github Issues <https://github.com/aws/aws-cloudtrail-processing-library/issues/>`_


Features
--------

* Provides functionality to continuously download CloudTrail log files in a fault tolerant and scalable manner.

* Serializes the events in JSON format to Plain Old Java Objects (POJO).

* Provides interfaces to implement your own business logic for selecting which events to process, processing events,
  handling errors, and handling log processing status updates.


Getting Started
---------------

Minimum Requirements
~~~~~~~~~~~~~~~~~~~~

* **AWS Java SDK 1.11.830**: To use the |library|, you'll need the `AWS Java SDK`__.
* **Java 1.8**: The |library| requires `Java 1.8 (Java SE 8)`__ or later.

.. __: https://github.com/aws/aws-sdk-java
.. __: http://www.oracle.com/technetwork/java/javase/overview/index.html


Documentation
~~~~~~~~~~~~~

To learn how to use the |library| to build a CloudTrail log processor in Java, read the documentation:

* `Using the CloudTrail Processing Library`__ in the *AWS CloudTrail User Guide*.
* `AWS CloudTrail Processing Library Reference`__

.. __: http://docs.aws.amazon.com/awscloudtrail/latest/userguide/using_processing_lib.html
.. __: http://docs.aws.amazon.com/awscloudtrail/latest/processinglib


Building From Source
--------------------

After you've downloaded the code from GitHub, you can build it using `Apache Maven`__. To disable GPG signing in the
build, use this command::

   mvn clean install -Dgpg.skip=true

.. __: http://maven.apache.org/


Release Notes
-------------

Release 1.4.0 (Jan 11, 2021)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Added support for parsing the following new top-level optional fields:

  * addendum
  * edgeDeviceDetails
  * tlsDetails
  * sessionCredentialFromConsole

* Updated the CloudTrail event version to 1.08.

Release 1.3.0 (Jul 30, 2020)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Added support for parsing new section, attributions, in insightContext.
* Added support for parsing new fields, baselineDuration, in statistics section in insightContext.
* Added thread configuration for s3 client, sqs client, and sqs reader to enable performance tuning.
* Updated minimum required Java SE version to 1.8.

Release 1.2.0 (Nov 20, 2019)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Added support for a new eventCategory attribute to indicate whether an event is a management, data, or Insights event.
* Added support for Insights events, including new attributes like insightDetails or insightContext.
* Updated the CloudTrail event version to 1.07.

Release 1.1.3 (Oct 18, 2018)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Added support for automatically deleting the initial SNS validation message sent whenever an SNS topic for a trail is configured or updated. In previous releases, these messages had to be manually deleted.

Release 1.1.2 (May 16, 2018)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Patch Release 1.1.1

Release 1.1.1 (Nov 30, 2017)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Added support for Boolean identification of management events.
* Updated the CloudTrail event version to 1.06.

Release 1.1.0 (Jun 1, 2017)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Add support for different formats for SQS messages from the same SQS queue to identify CloudTrail log files. This includes the following:

  * Notifications that CloudTrail sends to an SNS topic.
  * Notifications that Amazon S3 sends to an SNS topic.
  * Notifications that Amazon S3 sends directly to the SQS queue.

* Add support for the new deleteMessageUponFailure property. Use this property to delete messages that the CloudTrail Processing Library can't process, such as the following:

  * Parsing message failure:

    * File is not JSON.
    * Notification is not an `s3:ObjectCreated:Put event`__.
    * CloudTrail digest files, and other formats such as .jpeg or txt are unsupported.

  * Consuming log failure, such as processing events in a log file.

**Note**: If deleteMessageUponFailure is true, the CloudTrail Processing Library may delete messages that it can’t process. The default value is false. `Learn more`__.

.. __: http://docs.aws.amazon.com/AmazonS3/latest/dev/NotificationHowTo.html#notification-how-to-event-types-and-destinations
.. __: http://docs.aws.amazon.com/awscloudtrail/latest/userguide/use-the-cloudtrail-processing-library.html

Release 1.0.4 (Jan 17, 2017)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Add support for ARN prefix to identify the ARNPrefix associated with the resource. Resource must have either ARN or ARNPrefix, but not both.
* Add support for shared event ID to identify CloudTrail events from the same AWS action that is sent to different AWS accounts.
* Add support for VPC endpoint ID to identify the VPC endpoint in which requests were made from a VPC to another AWS service, such as Amazon S3.
* Add support for annotation to identify user provided annotation tagging delivered by CloudTrail.
* Add support for identity provider to identify the principal name of the external identity provider.

Release 1.0.3 (Oct 5, 2016)
~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Add support for service event, additional information is provided in the serviceEventDetails file.
* Add support for Resource type to identify the resource's type in a given CloudTrail event.
* Update AWS Java SDK to version 1.11.
* Update the latest supported CloudTrail event version to 1.05.
* Update event version is not supported by CloudTrail warning logging message to debug level.

Release 1.0.1 (Oct 28, 2015)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Update AWS Java SDK to version 1.10.

Release 1.0.0 (Nov 3, 2014)
~~~~~~~~~~~~~~~~~~~~~~~~~~~
* Initial release.
