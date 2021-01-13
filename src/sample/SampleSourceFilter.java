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

package sample;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.amazonaws.services.cloudtrail.processinglibrary.exceptions.CallbackException;
import com.amazonaws.services.cloudtrail.processinglibrary.interfaces.SourceFilter;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SQSBasedSource;
import com.amazonaws.services.cloudtrail.processinglibrary.model.SourceAttributeKeys;

public class SampleSourceFilter implements SourceFilter{
    /**
     * Max retry for a SQS message.
     */
    private static final int MAX_RECEIVED_COUNT = 3;

    /**
     * Account IDs would like to process.
     */
    private static List<String> accountIDs ;
    static {
        accountIDs = new ArrayList<>();
        accountIDs.add("123456789012");
        accountIDs.add("234567890123");
    }

    /**
     * This Sample Source Filter filter out messages that have been received more than 3 times and
     * accountIDs in a certain range.
     *
     * It is useful when you only want to retry on failed message up to certain times.
     */
    @Override
    public boolean filterSource(CloudTrailSource source) throws CallbackException {
        source = (SQSBasedSource) source;
        Map<String, String> sourceAttributes = source.getSourceAttributes();

        String accountId = sourceAttributes.get(SourceAttributeKeys.ACCOUNT_ID.getAttributeKey());
        String receivedCount = sourceAttributes.get(SourceAttributeKeys.APPROXIMATE_RECEIVE_COUNT.getAttributeKey());
        int approximateReceivedCount = Integer.parseInt(receivedCount);

        return approximateReceivedCount <= MAX_RECEIVED_COUNT && accountIDs.contains(accountId);
    }
}
