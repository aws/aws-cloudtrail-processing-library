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
package com.amazonaws.services.cloudtrail.clientlibrary.model.internal;

import java.util.Map;

/**
 * If the request was made with temporary security credentials, an element that provides information about
 * the session that was created for those credentials. Sessions are created when any API is called that
 * returns temporary credentials. Sessions are also created when users work in the console and when users
 * make a request using APIs that include multi-factor authentication.
 */
public class SessionContext extends CloudTrailDataStore{
    public SessionIssuer getSessionIssuer() {
        return (SessionIssuer) this.get(CloudTrailRecordField.sessionIssuer.name());
    }

    /**
     * Get Web IdentitySessionContext
     *
     * @return
     */
    public WebIdentitySessionContext getWebIdFederationData() {
        return (WebIdentitySessionContext) this.get(CloudTrailRecordField.webIdFederationData.name());
    }

    /**
     * Get attributes
     *
     * @return additional session context attributes
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, String> getAttributes() {
        return (Map) this.get(CloudTrailRecordField.attributes.name());
    }
}