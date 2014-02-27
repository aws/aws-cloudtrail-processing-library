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

/**
 * AWSCloudTrail User Identity objects holds information representing the identity making 
 * an AWS call.
 * 
 * @author simonguo
 */

public class UserIdentity {
    /**
     * User Identity type: "Root", "IAMUser", "FederatedUser", "Role", "AssumedRole" and "Unknown".
     */
    private String type;
    
    /**
     * 
     */
    private String principalId;
    
    /**
     * 
     */
    private String arn;
    
    /**
     * AWS Account Id.
     */
    private String accountId;
    
    /**
     * AWS Account Accesskey Id
     */
    private String accessKeyId;
    
    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private String invokedBy;
    
    /**
     *  AWSCloudTrailSessionContext is null for non-session identities
     */
    private SessionContext sessionContext;

      
    @SuppressWarnings("unused")
    private UserIdentity() {
    }
    
    public UserIdentity(String type, String principalId, String arn,
            String accountId, String accessKeyId, String userName) {
        setIdentityType(type);
        setPrincipalId(principalId);
        setARN(arn);
        setAccountId(accountId);
        setAccessKeyId(accessKeyId);
        setUserName(userName);
    }

    public String getIdentityType() {
        return type;
    }

    public void setIdentityType(String type) {
        this.type = type;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getARN() {
        return arn;
    }

    public void setARN(String arn) {
        this.arn = arn;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(String invokedBy) {
        this.invokedBy = invokedBy;
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    /**
     * Converts this AWSCloudTrailUserIdentity object to a String of the form.
     */
    @Override
    public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("{");
	    if (type != null) {
		    sb.append("type=");
		    sb.append(type);
		    sb.append(", ");
	    }
	    if (principalId != null) {
		    sb.append("principalId=");
		    sb.append(principalId);
		    sb.append(", ");
	    }
	    if (arn != null) {
		    sb.append("arn=");
		    sb.append(arn);
		    sb.append(", ");
	    }
	    if (accountId != null) {
		    sb.append("accountId=");
		    sb.append(accountId);
		    sb.append(", ");
	    }
	    if (accessKeyId != null) {
		    sb.append("accessKeyId=");
		    sb.append(accessKeyId);
		    sb.append(", ");
	    }
	    if (userName != null) {
		    sb.append("userName=");
		    sb.append(userName);
		    sb.append(", ");
	    }
	    if (invokedBy != null) {
		    sb.append("invokedBy=");
		    sb.append(invokedBy);
		    sb.append(", ");
	    }
	    if (sessionContext != null) {
		    sb.append("sessionContext=");
		    sb.append(sessionContext);
	    }
	    sb.append("}");
	    return sb.toString();
    }

    @Override
    public int hashCode() {
	    final int prime = 31;
	    int hashCode = 1;
	    hashCode = prime * hashCode + ((accessKeyId == null) ? 0 : accessKeyId.hashCode());
	    hashCode = prime * hashCode + ((accountId == null) ? 0 : accountId.hashCode());
	    hashCode = prime * hashCode + ((arn == null) ? 0 : arn.hashCode());
	    hashCode = prime * hashCode + ((invokedBy == null) ? 0 : invokedBy.hashCode());
	    hashCode = prime * hashCode + ((principalId == null) ? 0 : principalId.hashCode());
	    hashCode = prime * hashCode + ((sessionContext == null) ? 0 : sessionContext.hashCode());
	    hashCode = prime * hashCode + ((type == null) ? 0 : type.hashCode());
	    hashCode = prime * hashCode + ((userName == null) ? 0 : userName.hashCode());
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
	    UserIdentity other = (UserIdentity) obj;
	    if (accessKeyId == null) {
		    if (other.accessKeyId != null)
			    return false;
	    } else if (!accessKeyId.equals(other.accessKeyId))
		    return false;
	    if (accountId == null) {
		    if (other.accountId != null)
			    return false;
	    } else if (!accountId.equals(other.accountId))
		    return false;
	    if (arn == null) {
		    if (other.arn != null)
			    return false;
	    } else if (!arn.equals(other.arn))
		    return false;
	    if (invokedBy == null) {
		    if (other.invokedBy != null)
			    return false;
	    } else if (!invokedBy.equals(other.invokedBy))
		    return false;
	    if (principalId == null) {
		    if (other.principalId != null)
			    return false;
	    } else if (!principalId.equals(other.principalId))
		    return false;
	    if (sessionContext == null) {
		    if (other.sessionContext != null)
			    return false;
	    } else if (!sessionContext.equals(other.sessionContext))
		    return false;
	    if (type == null) {
		    if (other.type != null)
			    return false;
	    } else if (!type.equals(other.type))
		    return false;
	    if (userName == null) {
		    if (other.userName != null)
			    return false;
	    } else if (!userName.equals(other.userName))
		    return false;
	    return true;
    }
}