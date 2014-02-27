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
package com.amazonaws.services.cloudtrail.clientlibrary.serializer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.cloudtrail.clientlibrary.model.ClientRecord;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailRecord;
import com.amazonaws.services.cloudtrail.clientlibrary.model.RecordDeliveryInfo;
import com.amazonaws.services.cloudtrail.clientlibrary.model.SessionContext;
import com.amazonaws.services.cloudtrail.clientlibrary.model.SessionIssuer;
import com.amazonaws.services.cloudtrail.clientlibrary.model.UserIdentity;
import com.amazonaws.services.cloudtrail.clientlibrary.model.WebIdentitySessionContext;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AWSCloudTrailJacksonSerializer implements the AWSCloudTrailSerializer interface. A serializer reads content from
 * AWS CloudTrail log file and convert to AWSCloudTrailClientRecord object when calling getNextRecord() method. 
 * Underlining AWSCloudTrailJacksonSerializer use Fasterxml (Jackson) Json parser to parse AWS CloudTrail log file.
 */

public class AWSCloudTrailJacksonSerializer implements AWSCloudTrailSerializer, Closeable{
    private static final Log logger = LogFactory.getLog(AWSCloudTrailJacksonSerializer.class);

	private static final String ACCESS_KEY_ID = "accessKeyId";
	private static final String ACCOUNT_ID = "accountId";
	private static final String ADDITIONAL_EVENT_DATA = "additionalEventData";
	private static final String ARN = "arn"; 
	private static final String ATTRIBUTES = "attributes";
	private static final String AWS_REGION = "awsRegion";
	private static final String ERROR_CODE = "errorCode";
	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String EVENT_NAME = "eventName";
	private static final String EVENT_SOURCE = "eventSource";
	private static final String EVENT_TIME = "eventTime";
	private static final String EVENT_VERSION = "eventVersion";
	private static final String FEDERATED_PROVIDER = "federatedProvider";
	private static final String INVOKED_BY = "invokedBy";
	private static final String PRINCIPAL_ID = "principalId";
	private static final String REQUEST_PARAMETERS = "requestParameters";
	private static final String RESPONSE_ELEMENTS = "responseElements";
	private static final String SESSION_CONTEXT = "sessionContext";
	private static final String SESSION_ISSUER = "sessionIssuer";
	private static final String SOURCE_IP_ADDRESS = "sourceIPAddress";
	private static final String TYPE = "type";
	private static final String USER_AGENT = "userAgent";
	private static final String USER_IDENTITY = "userIdentity";
	private static final String USER_NAME = "userName";
	private static final String WEB_ID_FEDERATION_DATA = "webIdFederationData";

	/**
     * Jackson Json Parser
     */
    private JsonParser jsonParser;
    
    /**
     * An instance of AWSCloudTrailSource
     */
    private CloudTrailLog source;
    
    /**
     * Construct an instance of RecordSerializer object
     * 
     * @param inputBytes
     * @param s3ObjectKey 
     * @param s3BucketName 
     * @throws IOException
     */
    public AWSCloudTrailJacksonSerializer (InputStream inputStream, CloudTrailLog source) throws IOException {
    	this.source = source;

    	ObjectMapper mapper = new ObjectMapper();
    	JsonFactory jfactory = mapper.getFactory();
    	this.jsonParser = jfactory.createJsonParser(inputStream);
    	
    	this.readArrayHeader();
    }
    
    /**
     * Read off header part of AWS CloudTrail log.
     * @throws JsonParseException
     * @throws IOException
     */
    private void readArrayHeader() throws JsonParseException, IOException {
        if (this.jsonParser.nextToken() != JsonToken.START_OBJECT) {
        	throw new JsonParseException("Not a Json object", this.jsonParser.getCurrentLocation());
        }

        this.jsonParser.nextToken();
        if (!jsonParser.getText().equals("Records")) {
        	throw new JsonParseException("Not a CloudTrail log", this.jsonParser.getCurrentLocation());
        }
        
        if (this.jsonParser.nextToken() != JsonToken.START_ARRAY) {
        	throw new JsonParseException("Not a CloudTrail log", this.jsonParser.getCurrentLocation());
        }
    }
    
    /**
     * In Fasterxml parser, hasNextRecord will consume next token. So do not call it multiple times.
     */
    public boolean hasNextRecord() throws IOException {
		JsonToken nextToken = this.jsonParser.nextToken();
		return nextToken == JsonToken.START_OBJECT || nextToken == JsonToken.START_ARRAY;    		
    }
    
    /**
     * Close underlining jsonReader object, call it upon processed a log file
     * 
     * @throws IOException
     */
	public void close() throws IOException {
        this.jsonParser.close();
        this.jsonParser.close();
    }
    
    /**
     * Get next AWSCloudTrailClientRecord record from log file. When failed to parse a record, 
     * AWSCloudTrailClientParsingException will be thrown. In this case, the charEnd index 
     * the place we encountered parsing error.
     * @throws IOException 
     * @throws JsonParseException 
     */
    public ClientRecord getNextRecord() throws IOException {
    	CloudTrailRecord record = new CloudTrailRecord();
    	String key = null;
    	
    	// record's first character position in the log file.
    	long charStart = this.jsonParser.getCurrentLocation().getCharOffset();
    	
		while(this.jsonParser.nextToken() != JsonToken.END_OBJECT) {
			key = jsonParser.getCurrentName();
			
			if (key.equals(EVENT_VERSION)) {
				record.setEventVersion(jsonParser.nextTextValue());
			} else if (key.equals(USER_IDENTITY)) {
				this.parseUserIdentiy(record);
			} else if (key.equals(EVENT_TIME)) {
				record.setEventTime(convertToDate(jsonParser.nextTextValue()));
			} else if (key.equals(EVENT_SOURCE)) {
				record.setEventSource(jsonParser.nextTextValue());
			} else if (key.equals(EVENT_NAME)) {
				record.setEventName(jsonParser.nextTextValue());
			} else if (key.equals(AWS_REGION)) {
				record.setAwsRegion(jsonParser.nextTextValue());
			} else if (key.equals(SOURCE_IP_ADDRESS)) {
				record.setSourceIPAddress(jsonParser.nextTextValue());
			} else if (key.equals(USER_AGENT)) {
				record.setUserAgent(jsonParser.nextTextValue());
			} else if (key.equals(ERROR_CODE)) {
				record.setErrorCode(jsonParser.nextTextValue());
			} else if (key.equals(ERROR_MESSAGE)) {
				record.setErrorMessage(jsonParser.nextTextValue());
			} else if (key.equals(REQUEST_PARAMETERS)) {
				this.parseRequestParameters(record);
			} else if (key.equals(RESPONSE_ELEMENTS)) {
				this.parseResponseElements(record);
			} else if (key.equals(ADDITIONAL_EVENT_DATA)){
				this.parseAdditionalData(record);
			} else {
				this.jsonParser.skipChildren();
			}
		}
		this.setAccountId(record);

    	
    	// record's last character position in the log file.
    	long charEnd = this.jsonParser.getCurrentLocation().getCharOffset();
    	RecordDeliveryInfo deliveryInfo = new RecordDeliveryInfo(source, charStart, charEnd);

    	return new ClientRecord(record, deliveryInfo);
    }
    
    /**
     * Set AccountId in AWSCloudTrailRecord top level from either UserIdentity Top level or from
     * SessionIssuer. The AccountId in UserIdentity has higher precedence than AccountId in 
     * SessionIssuer (if exists). 
     * 
     * @param record
     */
    private void setAccountId(CloudTrailRecord record) {
        if (record.getUserIdentity() == null) {
            return ;
        }
        
        if (record.getUserIdentity().getAccessKeyId() != null) {
            record.setAccountId(record.getUserIdentity().getAccountId());
        } else {
            SessionContext sessionContext = record.getUserIdentity().getSessionContext();
            if (sessionContext != null && sessionContext.getSessionIssuer() != null) {
                record.setAccountId(sessionContext.getSessionIssuer().getAccountId());
            }
        }
    }

    /**
     * Parse user identity in AWSCloudTrailRecord
     * 
     * @param record
     * @throws IOException
     */
    private void parseUserIdentiy(CloudTrailRecord record) throws IOException {
    	
        if (this.jsonParser.nextToken() != JsonToken.START_OBJECT) {
        	throw new JsonParseException("Not a UserIdentiy object", this.jsonParser.getCurrentLocation());
        }
        
        String type = null;
        String principalId = null;
        String arn = null;
        String accountId = null;
        String accessKeyId = null;
        String userName = null;
        String invokedBy = null;
        SessionContext sessionContext = null;
        UserIdentity userIdentity = null;
        
        while (this.jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = this.jsonParser.getCurrentName();
            if (key.equals(TYPE)) {
                type = this.jsonParser.nextTextValue();
            } else if (key.equals(PRINCIPAL_ID)) {
                principalId = this.jsonParser.nextTextValue();
            } else if (key.equals(ARN)) {
                arn = this.jsonParser.nextTextValue();
            } else if (key.equals(ACCOUNT_ID)) {
                accountId = this.jsonParser.nextTextValue();
            } else if (key.equals(ACCESS_KEY_ID)) {
                accessKeyId = this.jsonParser.nextTextValue();
            } else if (key.equals(USER_NAME)) {
                userName = this.jsonParser.nextTextValue();
            } else if (key.equals(SESSION_CONTEXT)) {
                sessionContext = this.parseSessionContext();
            } else if (key.equals(INVOKED_BY)) {
                invokedBy = this.jsonParser.nextTextValue();
            } else {
            	this.jsonParser.nextToken();
            }
        }
        
        userIdentity = new UserIdentity(type, principalId, arn, accountId, accessKeyId, userName);
        userIdentity.setInvokedBy(invokedBy);
        userIdentity.setSessionContext(sessionContext);
        record.setUserIdentity(userIdentity);        
    }

    /**
     * Parse session context object
     * 
     * @return
     * @throws IOException
     */
    private SessionContext parseSessionContext() throws IOException {
        if (this.jsonParser.nextToken() != JsonToken.START_OBJECT) {
        	throw new JsonParseException("Not a SessionContext object", this.jsonParser.getCurrentLocation());
        }
        
        Map<String, String> attributes = null;
        SessionIssuer sessionIssuer = null;
        WebIdentitySessionContext webIdentitySessionContext = null;
        
        while (this.jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = this.jsonParser.getCurrentName();
            if (key.equals(ATTRIBUTES)) {
                attributes = this.parseAttributes();
            } else if (key.equals(SESSION_ISSUER)) {
                sessionIssuer = this.parseSessionIssuer();
            } else if (key.equals(WEB_ID_FEDERATION_DATA)) {
                webIdentitySessionContext = this.parseWebIdentitySessionContext();
            }
        }
        return new SessionContext(attributes, sessionIssuer, webIdentitySessionContext);
    }

    /**
     * Parse web identify session object
     * 
     * @return
     * @throws IOException
     */
    private WebIdentitySessionContext parseWebIdentitySessionContext() throws IOException {
    	
        if (this.jsonParser.nextToken() != JsonToken.START_OBJECT) {
        	throw new JsonParseException("Not a WebIdentitySessionContext object", this.jsonParser.getCurrentLocation());
        }
        
        String federatedProvider = null;
        Map<String, String> attributes = new HashMap<>();
        
        while (this.jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = this.jsonParser.getCurrentName();
            if (key.equals(ATTRIBUTES)) {
                attributes = this.parseAttributes();
            } else if (key.equals(FEDERATED_PROVIDER)) {
                federatedProvider = this.jsonParser.nextTextValue();
            } 
        }
        
        return new WebIdentitySessionContext(federatedProvider, attributes);
    }


    /**
     * Parse session issuer object. It only happened on role session and federated session.
     * 
     * @return
     * @throws IOException
     */
    private SessionIssuer parseSessionIssuer() throws IOException {
    	
        if (this.jsonParser.nextToken() != JsonToken.START_OBJECT) {
        	throw new JsonParseException("Not a SessionIssuer object", this.jsonParser.getCurrentLocation());
        }

        String type = null;
        String principalId = null;
        String arn = null;
        String accountId = null;
        String userName = null;
        
        while (this.jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = this.jsonParser.getCurrentName();
            if (key.equals(TYPE)) {
            	String nextValue = this.jsonParser.nextTextValue();
                type = nextValue;
            } else if (key.equals(PRINCIPAL_ID)) {
                principalId = this.jsonParser.nextTextValue();
            } else if (key.equals(ARN)) {
                arn = this.jsonParser.nextTextValue();
            } else if (key.equals(ACCOUNT_ID)) {
                accountId = this.jsonParser.nextTextValue();
            } else if (key.equals(USER_NAME)) {
                userName = this.jsonParser.nextTextValue();
            }
        }
                
        return new SessionIssuer(type, principalId, arn, accountId, userName);
    }


    /**
     * Parse session context attributes as a Map
     * 
     * @return
     * @throws IOException
     */
    private Map<String, String> parseAttributes() throws IOException {   
        if (this.jsonParser.nextToken() != JsonToken.START_OBJECT) {
        	throw new JsonParseException("Not a Attributes object", this.jsonParser.getCurrentLocation());
        }
        
        Map<String, String> attributes = new HashMap<>();
        
        while (this.jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = this.jsonParser.getCurrentName();
            String value = this.jsonParser.nextTextValue();
            attributes.put(key, value);
        }
                
        return attributes;
    }


    /**
     * Parse AWSCloudTrail request parameter
     * 
     * @param record
     * @throws IOException
     */
    private void parseRequestParameters(CloudTrailRecord record) throws IOException {
    	this.jsonParser.nextToken();
    	String requestParameter = null;
    	if (this.jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
    		JsonNode node = this.jsonParser.readValueAsTree();
    		requestParameter = node.toString();
    	}
    	record.setRequestParameters(requestParameter);  
    }
    
    /**
     * Parse AWSCloudTrail response parameter
     * 
     * @param record
     * @throws IOException
     */
    private void parseResponseElements(CloudTrailRecord record) throws IOException {
    	this.jsonParser.nextToken();
    	String responseElement = null;
    	if (this.jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
    		JsonNode node = this.jsonParser.readValueAsTree();
    		responseElement = node.toString();
    	}
    	record.setResponseElements(responseElement);
    }
    
    private void parseAdditionalData(CloudTrailRecord record) throws IOException {
    	this.jsonParser.nextToken();
    	String responseElement = null;
    	if (this.jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
    		JsonNode node = this.jsonParser.readValueAsTree();
    		responseElement = node.toString();
    	}
    	record.setAdditionalEventData(responseElement);
    }
    
    /**
     * This method convert a String to Date type. When parse error happened return current date.
     * @param s the String to convert to Date
     * @return Date 
     * @throws IOException 
     */
    private Date convertToDate(String s) throws IOException {
    	Date date = new Date();
    	try {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = sdf.parse(s);

		} catch (ParseException e) {
			throw new IOException("Cannot parse " + s + " as Date", e);
		}
		return date;
    }
}
