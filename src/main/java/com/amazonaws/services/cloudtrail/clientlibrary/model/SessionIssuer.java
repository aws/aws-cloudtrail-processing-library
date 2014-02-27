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
 * @author simonguo
 *
 */
public class SessionIssuer {
    private String type;
    private String principalId;
    private String arn;
    private String accountId;
    private String userName;
    
    @SuppressWarnings("unused")
    private SessionIssuer() {
        
    }
    public SessionIssuer(String type, String principalId, String arn, String accountId, String userName) {
        super();
        this.type = type;
        this.principalId = principalId;
        this.arn = arn;
        this.accountId = accountId;
        this.userName = userName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getPrincipalId() {
        return principalId;
    }
    
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
    
    public String getArn() {
        return arn;
    }
    
    public void setArn(String arn) {
        this.arn = arn;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Converts this AWSCloudTrailSessionIssuer object to a String of the form.
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
	    if (userName != null) {
		    sb.append("userName=");
		    sb.append(userName);
	    }
	    sb.append("}");
	    return sb.toString();
    }

    @Override
    public int hashCode() {
	    final int prime = 31;
	    int hashCode = 1;
	    hashCode = prime * hashCode + ((accountId == null) ? 0 : accountId.hashCode());
	    hashCode = prime * hashCode + ((arn == null) ? 0 : arn.hashCode());
	    hashCode = prime * hashCode + ((principalId == null) ? 0 : principalId.hashCode());
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
	    SessionIssuer other = (SessionIssuer) obj;
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
	    if (principalId == null) {
		    if (other.principalId != null)
			    return false;
	    } else if (!principalId.equals(other.principalId))
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
