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
package com.amazonaws.services.cloudtrail.clientlibrary.model;

import java.util.Map;

/**
 * 
 */
public class SessionContext {

    private Map<String, String> attributes;

    private SessionIssuer sessionIssuer;

    private WebIdentitySessionContext webIdFederationData;
     
    @SuppressWarnings("unused")
    private SessionContext() {
    }
    
    public SessionContext(Map<String, String> attributes,
            SessionIssuer sessionIssuer,
            WebIdentitySessionContext webIdFederationData) {
        setAttributes(attributes);
        setSessionIssuer(sessionIssuer);
        setWebIdFederationData(webIdFederationData);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public SessionIssuer getSessionIssuer() {
        return sessionIssuer;
    }

    public void setSessionIssuer(SessionIssuer sessionIssuer) {
        this.sessionIssuer = sessionIssuer;
    }

    public WebIdentitySessionContext getWebIdFederationData() {
        return webIdFederationData;
    }

    public void setWebIdFederationData(
            WebIdentitySessionContext webIdFederationData) {
        this.webIdFederationData = webIdFederationData;
    }

    /**
     * Converts this AWSCloudTrailSessionContext object to a String of the form.
     */
    @Override
    public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("{");
	    if (attributes != null) {
		    builder.append("attributes=");
		    builder.append(attributes);
		    builder.append(", ");
	    }
	    if (sessionIssuer != null) {
		    builder.append("sessionIssuer=");
		    builder.append(sessionIssuer);
		    builder.append(", ");
	    }
	    if (webIdFederationData != null) {
		    builder.append("webIdFederationData=");
		    builder.append(webIdFederationData);
	    }
	    builder.append("}");
	    return builder.toString();
    }

    @Override
    public int hashCode() {
	    final int prime = 31;
	    int hashCode = 1;
	    hashCode = prime * hashCode + ((attributes == null) ? 0 : attributes.hashCode());
	    hashCode = prime * hashCode + ((sessionIssuer == null) ? 0 : sessionIssuer.hashCode());
	    hashCode = prime * hashCode + ((webIdFederationData == null) ? 0 : webIdFederationData.hashCode());
	    return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    SessionContext other = (SessionContext) obj;
	    if (attributes == null) {
		    if (other.attributes != null)
			    return false;
	    } else if (!attributes.equals(other.attributes))
		    return false;
	    if (sessionIssuer == null) {
		    if (other.sessionIssuer != null)
			    return false;
	    } else if (!sessionIssuer.equals(other.sessionIssuer))
		    return false;
	    if (webIdFederationData == null) {
		    if (other.webIdFederationData != null)
			    return false;
	    } else if (!webIdFederationData.equals(other.webIdFederationData))
		    return false;
	    return true;
    }
}