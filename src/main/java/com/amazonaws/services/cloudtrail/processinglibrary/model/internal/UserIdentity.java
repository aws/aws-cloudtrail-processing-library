/*******************************************************************************
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 * Information about the user that made a request is included in the userIdentity element.
 * This information can help you determine how your AWS resources have been accessed.
 */
public class UserIdentity extends CloudTrailDataStore{
    /**
     * Get identity type
     *
     * @return The type of the principal that made the call
     */
    public String getIdentityType() {
        return (String) this.get(CloudTrailEventField.type.name());
    }

    /**
     * Get principal ID
     *
     * @return A unique identifier for the principal. For requests made with temporary
     * security credentials, this value includes the session name that is passed to
     * the AssumeRole, AssumeRoleWIthWebIdentity, or GetFederationToken API call.
     */
    public String getPrincipalId() {
        return (String) this.get(CloudTrailEventField.principalId.name());
    }

    /**
     * Get principal ARN
     *
     * @return The Amazon Resource Name (ARN) of the principal that made the call.
     */
    public String getARN() {
        return (String) this.get(CloudTrailEventField.arn.name());
    }

    /**
     * Get account ID
     *
     * @return The account that owns the entity that granted permissions for the
     * request. If the request was made using temporary security credentials, this
     * is the account that owns the IAM user or role that was used to obtain credentials.
     */
    public String getAccountId() {
        return (String) this.get(CloudTrailEventField.accountId.name());
    }

    /**
     * Get access key ID
     *
     * @return The access key ID that was used to sign the request. If the request
     * was made using temporary security credentials, this is the access key ID of
     * the temporary credentials.
     */
    public String getAccessKeyId() {
        return (String) this.get(CloudTrailEventField.accessKeyId.name());
    }

    /**
     * Get user name
     *
     * @return Friendly name of the principal that made the call.
     */
    public String getUserName() {
        return (String) this.get(CloudTrailEventField.userName.name());
    }

    /**
     * Get invoked by
     *
     * @return If the request was made by another AWS service, such as Auto
     * Scaling or AWS Elastic Beanstalk, the name of the service
     */
    public String getInvokedBy() {
        return (String) this.get(CloudTrailEventField.invokedBy.name());
    }

    /**
     * Get session context
     *
     * @return {@link SessionContext} If the request was made with temporary security credentials, an element
     * that provides information about the session that was created for those credentials
     */
    public SessionContext getSessionContext() {
        return (SessionContext) this.get(CloudTrailEventField.sessionContext.name());
    }
}