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

* **AWS Java SDK 1.10.27**: In order to use the |library|, you'll need the `AWS Java SDK`__.
* **Java 1.7**: The |library| requires `Java 1.7 (Java SE 7)`__ or later.

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

Release 1.0.4 (Jan 17, 2017)
~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
