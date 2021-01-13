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

package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

/**
 * AWS resources.
 */
public class Resource extends CloudTrailDataStore {
    /**
     * Get the Amazon Resource Name (ARN) for this resource.
     *
     * @return the ARN associated with the resource.
     */
    public String getArn() {
        return (String) get(CloudTrailEventField.ARN.name());
    }

    /**
     * Get the ARNPrefix of the resource.
     *
     * Resource must have either ARN or ARNPrefix, but not both.
     *
     * @return the ARNPrefix associated with the resource.
     */
    public String getArnPrefix() {
        return (String) get(CloudTrailEventField.ARNPrefix.name());
    }

    /**
     * Get the account ID associated with the resource.
     *
     * This value could be null
     *
     * @return the account ID
     */
    public String getAccountId() {
        return (String) get(CloudTrailEventField.accountId.name());
    }

    /**
     *
     * Get resource type
     *
     * @return the type of resource. e.g. AWS::IAM::Role
     */
    public String getType() {
        return (String) get(CloudTrailEventField.type.name());
    }
}
