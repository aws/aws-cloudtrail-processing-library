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

import java.util.Date;

/**
 * This a record that AWSCloudTrail pushed to user's S3 bucket.
 */
public class CloudTrailRecord {
    
    /**
     * The numeric version of the log event format.
     */
    private String eventVersion;

    /**
     * The object contains the caller identify information.
     */
    private UserIdentity userIdentity;
    
    /**
     * This represents the event's start time in UTC.
     */
    private Date eventTime;
    
    /**
     * Event name is the the Public AWS API call. The case should match the API definition. 
     */
    private String eventName;
    
    /**
     * This is the AWS service principal name.
     */
    private String eventSource;
    
    /**
     * This is the hyphenated name of the AWS Region (in lowercase letters), where the activity occurred. Ex: us-east-1
     */
    private String awsRegion;

    /**
     * This is the apparent IPV4 address of the customer, not the console web server.
     */
    private String sourceIPAddress;
    
    /**
     * The agent through which the AWS activity was performed. User agent information is logged as 
     * provided in the API headers without any modification.
     */
    private String userAgent;
    
    /**
     * AWS API call request Id.
     */
    private String requestId;

    /**
     * A string that is emitted by the service for any client or server error codes.
     */
    private String errorCode;
    
    /**
     * Error message emitted by the API Call, including messages for authorization failures.
     */
    private String errorMessage;
    
    /**
     * AWS API Call request parameters.
     */
    private String requestParameters;
    
    /**
     * AWS API Call response parameters.
     */
    private String responseElements;
    
    /**
     * AWS Services can provide additional data that was not part of request/response, but may be of value to 
     * someone viewing the logs.
     */
    private String additionalEventData;
    
    /**
     * The object contains log file delivery info where the record extracted from.
     */
    private RecordDeliveryInfo deliveryInfo;
    
    /**
     * AWS Account Id.
     */
    private String accountId;

    /**
     * Get API event version.
     * @return
     */
    public String getEventVersion() {
        return eventVersion;
    }

    /**
     * Set API event version.
     * @param eventVersion
     */
    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }

    /**
     * Get User Identify object.
     * @return
     */
    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    /**
     * Set User Identity object.
     * @param userIdentity
     */
    public void setUserIdentity(UserIdentity userIdentity) {
        this.userIdentity = userIdentity;
    }

    /**
     * Get API event timestamp.
     * @return
     */
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * Set API event timestamp.
     * @param eventTime
     */
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Get API event name.
     * @return
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Set API event name.
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Get API event source.
     * @return
     */
    public String getEventSource() {
        return eventSource;
    }

    /**
     * Set API event source.
     * @param eventSource
     */
    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    /**
     * Get AWS region where API event occurred.
     * @return
     */
    public String getAwsRegion() {
        return awsRegion;
    }

    /**
     * Get AWS region where API event occurred.
     * @param awsRegion
     */
    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    /**
     * Get API event source IP address.
     * @return
     */
    public String getSourceIPAddress() {
        return sourceIPAddress;
    }

    /**
     * Set API event source IP address.
     * @param sourceIPAddress
     */
    public void setSourceIPAddress(String sourceIPAddress) {
        this.sourceIPAddress = sourceIPAddress;
    }

    /**
     * Get API event user agent.
     * @return
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Set API event user agent.
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Get API request ID.
     * @return
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Set API request ID.
     * @param requestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Get API call error code. Null if no error.
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Set API call error code.
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Get API call error message. Null if no error.
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set API call error message.
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get API call request parameters.
     * @return
     */
    public String getRequestParameters() {
        return requestParameters;
    }

    /**
     * Set API call request parameters.
     * @param requestParameters
     */
    public void setRequestParameters(String requestParameters) {
        this.requestParameters = requestParameters;
    }

    /**
     * Get API call response elements.
     * @return
     */
    public String getResponseElements() {
        return responseElements;
    }

    /**
     * Set API call response elements.
     * @param responseElements
     */
    public void setResponseElements(String responseElements) {
        this.responseElements = responseElements;
    }

    /**
     * Get additional API call data set by AWS services.
     * @return
     */
    public String getAdditionalEventData() {
        return additionalEventData;
    }

    /**
     * Set additional API call data set by AWS services.
     * @param additionalEventData
     */
    public void setAdditionalEventData(String additionalEventData) {
        this.additionalEventData = additionalEventData;
    }

    /**
     * Get log delivery information
	 * @return the deliveryInfo
	 */
	public RecordDeliveryInfo getDeliveryInfo() {
		return deliveryInfo;
	}

	/**
	 * Set log delivery information
	 * @param deliveryInfo the deliveryInfo to set
	 */
	public void setDeliveryInfo(RecordDeliveryInfo deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
	}

	/**
     * Get AWS account ID made API call.
     * @return
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Set AWS account ID make API call.
     * @param accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    

	/**
	 * Converts this AWSCloudTrailRecord object to a String of the form.
	 */
	@Override
    public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("{");
	    if (eventVersion != null) {
		    sb.append("eventVersion=");
		    sb.append(eventVersion);
		    sb.append(", ");
	    }
	    if (userIdentity != null) {
		    sb.append("userIdentity=");
		    sb.append(userIdentity);
		    sb.append(", ");
	    }
	    if (eventTime != null) {
		    sb.append("eventTime=");
		    sb.append(eventTime);
		    sb.append(", ");
	    }
	    if (eventName != null) {
		    sb.append("eventName=");
		    sb.append(eventName);
		    sb.append(", ");
	    }
	    if (eventSource != null) {
		    sb.append("eventSource=");
		    sb.append(eventSource);
		    sb.append(", ");
	    }
	    if (awsRegion != null) {
		    sb.append("awsRegion=");
		    sb.append(awsRegion);
		    sb.append(", ");
	    }
	    if (sourceIPAddress != null) {
		    sb.append("sourceIPAddress=");
		    sb.append(sourceIPAddress);
		    sb.append(", ");
	    }
	    if (userAgent != null) {
		    sb.append("userAgent=");
		    sb.append(userAgent);
		    sb.append(", ");
	    }
	    if (requestId != null) {
		    sb.append("requestId=");
		    sb.append(requestId);
		    sb.append(", ");
	    }
	    if (errorCode != null) {
		    sb.append("errorCode=");
		    sb.append(errorCode);
		    sb.append(", ");
	    }
	    if (errorMessage != null) {
		    sb.append("errorMessage=");
		    sb.append(errorMessage);
		    sb.append(", ");
	    }
	    if (requestParameters != null) {
		    sb.append("requestParameters=");
		    sb.append(requestParameters);
		    sb.append(", ");
	    }
	    if (responseElements != null) {
		    sb.append("responseElements=");
		    sb.append(responseElements);
		    sb.append(", ");
	    }
	    if (additionalEventData != null) {
		    sb.append("additionalEventData=");
		    sb.append(additionalEventData);
		    sb.append(", ");
	    }
	    if (accountId != null) {
		    sb.append("accountId=");
		    sb.append(accountId);
	    }
	    sb.append("}");
	    return sb.toString();
    }

	/**
	 * Returns a hash code value for this object.
	 */
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int hashCode = 1;
	    hashCode = prime * hashCode + ((accountId == null) ? 0 : accountId.hashCode());
	    hashCode = prime * hashCode + ((additionalEventData == null) ? 0 : additionalEventData.hashCode());
	    hashCode = prime * hashCode + ((awsRegion == null) ? 0 : awsRegion.hashCode());
	    hashCode = prime * hashCode + ((errorCode == null) ? 0 : errorCode.hashCode());
	    hashCode = prime * hashCode + ((errorMessage == null) ? 0 : errorMessage.hashCode());
	    hashCode = prime * hashCode + ((eventName == null) ? 0 : eventName.hashCode());
	    hashCode = prime * hashCode + ((eventSource == null) ? 0 : eventSource.hashCode());
	    hashCode = prime * hashCode + ((eventTime == null) ? 0 : eventTime.hashCode());
	    hashCode = prime * hashCode + ((eventVersion == null) ? 0 : eventVersion.hashCode());
	    hashCode = prime * hashCode + ((requestId == null) ? 0 : requestId.hashCode());
	    hashCode = prime * hashCode + ((requestParameters == null) ? 0 : requestParameters.hashCode());
	    hashCode = prime * hashCode + ((responseElements == null) ? 0 : responseElements.hashCode());
	    hashCode = prime * hashCode + ((sourceIPAddress == null) ? 0 : sourceIPAddress.hashCode());
	    hashCode = prime * hashCode + ((userAgent == null) ? 0 : userAgent.hashCode());
	    hashCode = prime * hashCode + ((userIdentity == null) ? 0 : userIdentity.hashCode());
	    return hashCode;
    }

	/**
	 * Compares two AWSCloudTrailRecord for equality.
	 */
	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    CloudTrailRecord other = (CloudTrailRecord) obj;
	    
	    if (accountId == null) {
		    if (other.accountId != null)
			    return false;
	    } else if (!accountId.equals(other.accountId))
		    return false;
	    if (additionalEventData == null) {
		    if (other.additionalEventData != null)
			    return false;
	    } else if (!additionalEventData.equals(other.additionalEventData))
		    return false;
	    if (awsRegion == null) {
		    if (other.awsRegion != null)
			    return false;
	    } else if (!awsRegion.equals(other.awsRegion))
		    return false;
	    if (errorCode == null) {
		    if (other.errorCode != null)
			    return false;
	    } else if (!errorCode.equals(other.errorCode))
		    return false;
	    if (errorMessage == null) {
		    if (other.errorMessage != null)
			    return false;
	    } else if (!errorMessage.equals(other.errorMessage))
		    return false;
	    if (eventName == null) {
		    if (other.eventName != null)
			    return false;
	    } else if (!eventName.equals(other.eventName))
		    return false;
	    if (eventSource == null) {
		    if (other.eventSource != null)
			    return false;
	    } else if (!eventSource.equals(other.eventSource))
		    return false;
	    if (eventTime == null) {
		    if (other.eventTime != null)
			    return false;
	    } else if (!eventTime.equals(other.eventTime))
		    return false;
	    if (eventVersion == null) {
		    if (other.eventVersion != null)
			    return false;
	    } else if (!eventVersion.equals(other.eventVersion))
		    return false;
	    if (requestId == null) {
		    if (other.requestId != null)
			    return false;
	    } else if (!requestId.equals(other.requestId))
		    return false;
	    if (requestParameters == null) {
		    if (other.requestParameters != null)
			    return false;
	    } else if (!requestParameters.equals(other.requestParameters))
		    return false;
	    if (responseElements == null) {
		    if (other.responseElements != null)
			    return false;
	    } else if (!responseElements.equals(other.responseElements))
		    return false;
	    if (sourceIPAddress == null) {
		    if (other.sourceIPAddress != null)
			    return false;
	    } else if (!sourceIPAddress.equals(other.sourceIPAddress))
		    return false;
	    if (userAgent == null) {
		    if (other.userAgent != null)
			    return false;
	    } else if (!userAgent.equals(other.userAgent))
		    return false;
	    if (userIdentity == null) {
		    if (other.userIdentity != null)
			    return false;
	    } else if (!userIdentity.equals(other.userIdentity))
		    return false;
	    return true;
    }
}
