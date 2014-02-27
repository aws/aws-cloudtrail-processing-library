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
 * @author simonguo
 *
 */
public class WebIdentitySessionContext {
    private String federatedProvider;
    private Map<String, String> attributes;
 
    @SuppressWarnings("unused")
    private WebIdentitySessionContext() {
    }
    
    public WebIdentitySessionContext(String federatedProvider, Map<String, String> attributes) {
        setFederatedProvider(federatedProvider);
        setAttributes(attributes);
    }

    public String getFederatedProvider() {
        return federatedProvider;
    }

    public void setFederatedProvider(String federatedProvider) {
        this.federatedProvider = federatedProvider;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Converts this AWSCloudTrailWebIdentitySessionContext object to a String of the form.
     */
    @Override
    public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("{");
	    if (federatedProvider != null) {
		    sb.append("federatedProvider=");
		    sb.append(federatedProvider);
		    sb.append(", ");
	    }
	    if (attributes != null) {
		    sb.append("attributes=");
		    sb.append(attributes);
	    }
	    sb.append("}");
	    return sb.toString();
    }

    @Override
    public int hashCode() {
	    final int prime = 31;
	    int hashCode = 1;
	    hashCode = prime * hashCode + ((attributes == null) ? 0 : attributes.hashCode());
	    hashCode = prime * hashCode + ((federatedProvider == null) ? 0 : federatedProvider.hashCode());
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
	    WebIdentitySessionContext other = (WebIdentitySessionContext) obj;
	    if (attributes == null) {
		    if (other.attributes != null)
			    return false;
	    } else if (!attributes.equals(other.attributes))
		    return false;
	    if (federatedProvider == null) {
		    if (other.federatedProvider != null)
			    return false;
	    } else if (!federatedProvider.equals(other.federatedProvider))
		    return false;
	    return true;
    }


}