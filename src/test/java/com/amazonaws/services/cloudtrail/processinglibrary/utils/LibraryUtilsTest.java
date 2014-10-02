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
package com.amazonaws.services.cloudtrail.processinglibrary.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;

public class LibraryUtilsTest {
	String s3ObjectHttpUrl = "http://s3-us-west-2.amazonaws.com/mybucket/myobjectpath1/myobjectpath2/myobject.extension";
	String objectKeyWithOutPrefix = "https://s3-us-west-2.amazonaws.com/mybucket/AWSLogs/123456789012/CloudTrail/us-east-1/2014/02/14/123456789012_CloudTrail_us-east-1_20140214T2230Z_K0UsfksWvF8TBJZy.json.gz";
	String objectKeyWithPrefix = "https://s3-us-west-2.amazonaws.com/mybucket/myprefix/AWSLogs/123456789012/CloudTrail/us-east-1/2014/02/14/123456789012_CloudTrail_us-east-1_20140214T2230Z_K0UsfksWvF8TBJZy.json.gz";
	String objectKeyWithAWSLogsPrefix = "https://s3-us-west-2.amazonaws.com/mybucket/AWSLogs/AWSLogs/123456789012/CloudTrail/us-east-1/2014/02/14/123456789012_CloudTrail_us-east-1_20140214T2230Z_K0UsfksWvF8TBJZy.json.gz";

	@Test
	public void testToBucketNameObjectKey() {
		String[] result = LibraryUtils.toBucketNameObjectKey(s3ObjectHttpUrl);
		assertEquals("mybucket", result[0]);
		assertEquals("myobjectpath1/myobjectpath2/myobject.extension", result[1]);
	}
	
	@Test
	public void testToBucketNameObjectKeyWithGlobalRegion() {
		String[] result = LibraryUtils.toBucketNameObjectKey(s3ObjectHttpUrl);
		assertEquals("mybucket", result[0]);
		assertEquals("myobjectpath1/myobjectpath2/myobject.extension", result[1]);
	}
	
	@Test
	public void testToBucketNameObjectKeyWithRandomString() {
		String s3ObjectHttpUrl = "myrandomstring";
		String[] result = LibraryUtils.toBucketNameObjectKey(s3ObjectHttpUrl);
		assertNull(result);
	}
	
	@Test
	public void testExtractAccoundIdFromObjectKeyWithoutPrefix() {
		assertEquals("123456789012", LibraryUtils.extractAccountIdFromObjectKey(objectKeyWithOutPrefix));
	}
	
	@Test
	public void testExtractAccoundIdFromObjectKeyWithPrefix() {
		assertEquals("123456789012", LibraryUtils.extractAccountIdFromObjectKey(objectKeyWithPrefix));
	}
	
	@Test
	public void testExtractAccoundIdFromObjectKeyWithAWSLogsPrefix() {
		assertEquals("123456789012", LibraryUtils.extractAccountIdFromObjectKey(objectKeyWithAWSLogsPrefix));
	}
}
