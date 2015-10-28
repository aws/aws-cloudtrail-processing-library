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

Library Release 1.0.1 (October 28, 2015)
Initial release.
