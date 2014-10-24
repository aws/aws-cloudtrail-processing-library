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

import org.junit.Test;

import com.amazonaws.services.cloudtrail.processinglibrary.factory.ThreadPoolFactory;
public class ThreadPoolFactoryTest {

    @Test(expected = IllegalStateException.class)
    public void testCreateMainThreadPool() {
        ThreadPoolFactory executionFactory = new ThreadPoolFactory(0, null);
        executionFactory.createMainThreadPool();
    }
}
