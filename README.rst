.. |library| replace:: AWS CloudTrail Processing Library
.. |ct| replace:: AWS CloudTrail
.. |sqs| replace:: Amazon SQS
.. |s3| replace:: Amazon S3

AWS CloudTrail Processing Library
=================================

The **|library|** is a Java client library that makes it easy to build an application that reads and processes CloudTrail log files in a fault tolerant and highly scalable manner.

* `CloudTrail Product Page <http://aws.amazon.com/cloudtrail/>`_
* `CloudTrail Forum <https://forums.aws.amazon.com/forum.jspa?forumID=168/>`_
* `Github Issues <https://github.com/aws/aws-cloudtrail-processing-library/issues/>`_


Features
--------

* Provides functionality to continuously download CloudTrail log files in a fault tolerant and scalable manner.

* Serializes the events in JSON format to Plain Old Java Objects (POJO).

* Provides interfaces to implement your own business logic.

Getting Started
---------------

**Minimum Requirements**

* **AWS Java SDK 1.8**: In order to use the |library|, you'll need the `AWS Java SDK`__.
* **Java 1.7**: The |library| requires `Java 1.7 (Java SE 7)`__ or later.

.. __: https://github.com/aws/aws-sdk-java
.. __: http://www.oracle.com/technetwork/java/javase/overview/index.html


Building From Source
--------------------

After you've downloaded the code from GitHub, you can build it using `Apache Maven`__. To disable GPG signing in the
build, use this command::

   mvn clean install -Dgpg.skip=true

.. __: http://maven.apache.org/



Release Notes
-------------

Release 1.0 (November 3, 2014)
Initial release.