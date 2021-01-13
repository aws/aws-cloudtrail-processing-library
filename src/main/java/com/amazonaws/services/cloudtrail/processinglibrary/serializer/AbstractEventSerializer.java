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

package com.amazonaws.services.cloudtrail.processinglibrary.serializer;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEvent;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventData;
import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.*;
import com.amazonaws.services.cloudtrail.processinglibrary.utils.LibraryUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Abstract base class for Event Serializer implementations.
 */
public abstract class AbstractEventSerializer implements EventSerializer {

    private static final Log logger = LogFactory.getLog(AbstractEventSerializer.class);
    private static final String RECORDS = "Records";
    private static final double SUPPORTED_EVENT_VERSION = 1.08d;

    /**
     * A Jackson JSON Parser object.
     */
    private JsonParser jsonParser;

    /**
     * Construct an AbstractEventSerializer object
     *
     * @param jsonParser a Jackson
     * <a href="http://jackson.codehaus.org/1.4.0/javadoc/org/codehaus/jackson/JsonParser.html">JsonParser</a> object to
     *     use for interpreting JSON objects.
     * @throws IOException under no conditions.
     */
    public AbstractEventSerializer (JsonParser jsonParser) throws IOException {
        this.jsonParser = jsonParser;
    }

    /**
     * An abstract class that returns an
     * {@link com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailEventMetadata} object.
     *
     * @param charStart the character count at which to begin reading event data.
     * @param charEnd the character count at which to stop reading event data.
     * @return the event metadata.
     */
    public abstract CloudTrailEventMetadata getMetadata(int charStart, int charEnd);

    /**
     * Read the header of an AWS CloudTrail log.
     *
     * @throws JsonParseException if the log could not be parsed.
     * @throws IOException if the log could not be opened or accessed.
     */
    protected void readArrayHeader() throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a Json object", jsonParser.getCurrentLocation());
        }

        jsonParser.nextToken();
        if (!jsonParser.getText().equals(RECORDS)) {
            throw new JsonParseException("Not a CloudTrail log", jsonParser.getCurrentLocation());
        }

        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new JsonParseException("Not a CloudTrail log", jsonParser.getCurrentLocation());
        }
    }

    /**
     * Indicates whether the CloudTrail log has more events to read.
     *
     * @return <code>true</code> if the log contains more events; <code>false</code> otherwise.
     * @throws IOException if the log could not be opened or accessed.
     */
    public boolean hasNextEvent() throws IOException {
        /* In Fasterxml parser, hasNextEvent will consume next token. So do not call it multiple times. */
        JsonToken nextToken = jsonParser.nextToken();
        return nextToken == JsonToken.START_OBJECT || nextToken == JsonToken.START_ARRAY;
    }

    /**
     * Close the JSON parser object used to read the CloudTrail log.
     *
     * @throws IOException if the log could not be opened or accessed.
     */
    public void close() throws IOException {
        jsonParser.close();
    }

    /**
     * Get the next event from the CloudTrail log and parse it.
     *
     * @return a {@link CloudTrailEvent} that represents the
     *     parsed event.
     * @throws IOException if the event could not be parsed.
     */
    public CloudTrailEvent getNextEvent() throws IOException {
        CloudTrailEventData eventData = new CloudTrailEventData();
        String key;

         /* Get next CloudTrailEvent event from log file. When failed to parse a event,
         * IOException will be thrown. In this case, the charEnd index the place we
         * encountered parsing error. */

        // return the starting location of the current token; that is, position of the first character
        // from input that starts the current token
        int charStart = (int) jsonParser.getTokenLocation().getCharOffset();

        while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
            key = jsonParser.getCurrentName();

            switch (key) {
            case "eventVersion":
                String eventVersion = jsonParser.nextTextValue();
                if (Double.parseDouble(eventVersion) > SUPPORTED_EVENT_VERSION) {
                    logger.debug(String.format("EventVersion %s is not supported by CloudTrail.", eventVersion));
                }
                eventData.add(key, eventVersion);
                break;
            case "userIdentity":
                this.parseUserIdentity(eventData);
                break;
            case "eventTime":
                eventData.add(CloudTrailEventField.eventTime.name(), convertToDate(jsonParser.nextTextValue()));
                break;
            case "eventID":
                eventData.add(key, convertToUUID(jsonParser.nextTextValue()));
                break;
            case "readOnly":
                this.parseReadOnly(eventData);
                break;
            case "resources":
                this.parseResources(eventData);
                break;
            case "managementEvent":
                this.parseManagementEvent(eventData);
                break;
            case "insightDetails":
                this.parseInsightDetails(eventData);
                break;
            case "addendum":
                this.parseAddendum(eventData);
                break;
            case "tlsDetails":
                this.parseTlsDetails(eventData);
                break;
            default:
                eventData.add(key, parseDefaultValue(key));
                break;
            }
        }
        this.setAccountId(eventData);

        // event's last character position in the log file.
        int charEnd = (int) jsonParser.getTokenLocation().getCharOffset();

        CloudTrailEventMetadata metaData = getMetadata(charStart, charEnd);

        return new CloudTrailEvent(eventData, metaData);
    }

    /**
     * Set AccountId in CloudTrailEventData top level from either recipientAccountID or from UserIdentity.
     * If recipientAccountID exists then recipientAccountID is set to accountID; otherwise, accountID is retrieved
     * from UserIdentity.
     *
     * There are 2 places accountID would appear in UserIdentity: first is the UserIdentity top level filed
     * and the second place is accountID inside SessionIssuer. If accountID exists in the top level field, then it is
     * set to accountID; otherwise, accountID is retrieved from SessionIssuer.
     *
     * If all 3 places cannot find accountID, then accountID is not set.
     *
     * @param eventData the event data to set.
     */
    private void setAccountId(CloudTrailEventData eventData) {
        if (eventData.getRecipientAccountId() != null) {
            eventData.add("accountId", eventData.getRecipientAccountId());
            return;
        }

        if (eventData.getUserIdentity() != null &&
            eventData.getUserIdentity().getAccountId() != null) {
            eventData.add("accountId", eventData.getUserIdentity().getAccountId());
            return;
        }

        if (eventData.getUserIdentity() != null &&
            eventData.getUserIdentity().getAccountId() == null &&
            eventData.getUserIdentity().getSessionContext() != null &&
            eventData.getUserIdentity().getSessionContext().getSessionIssuer() != null &&
            eventData.getUserIdentity().getSessionContext().getSessionIssuer().getAccountId() != null) {
            eventData.add("accountId", eventData.getUserIdentity().getSessionContext().getSessionIssuer().getAccountId());
        }
    }

    /**
     * Parses the {@link UserIdentity} in CloudTrailEventData
     *
     * @param eventData {@link CloudTrailEventData} needs to parse.
     * @throws IOException
     */
    private void parseUserIdentity(CloudTrailEventData eventData) throws IOException {
        JsonToken nextToken = jsonParser.nextToken();
        if (nextToken == JsonToken.VALUE_NULL) {
            eventData.add(CloudTrailEventField.userIdentity.name(), null);
            return;
        }

        if (nextToken != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a UserIdentity object", jsonParser.getCurrentLocation());
        }

        UserIdentity userIdentity = new UserIdentity();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
            case "type":
                userIdentity.add(CloudTrailEventField.type.name(), jsonParser.nextTextValue());
                break;
            case "principalId":
                userIdentity.add(CloudTrailEventField.principalId.name(), jsonParser.nextTextValue());
                break;
            case "arn":
                userIdentity.add(CloudTrailEventField.arn.name(), jsonParser.nextTextValue());
                break;
            case "accountId":
                userIdentity.add(CloudTrailEventField.accountId.name(), jsonParser.nextTextValue());
                break;
            case "accessKeyId":
                userIdentity.add(CloudTrailEventField.accessKeyId.name(), jsonParser.nextTextValue());
                break;
            case "userName":
                userIdentity.add(CloudTrailEventField.userName.name(), jsonParser.nextTextValue());
                break;
            case "sessionContext":
                this.parseSessionContext(userIdentity);
                break;
            case "invokedBy":
                userIdentity.add(CloudTrailEventField.invokedBy.name(), jsonParser.nextTextValue());
                break;
            case "identityProvider":
                userIdentity.add(CloudTrailEventField.identityProvider.name(), jsonParser.nextTextValue());
                break;
            default:
                userIdentity.add(key, parseDefaultValue(key));
                break;
            }
        }
        eventData.add(CloudTrailEventField.userIdentity.name(), userIdentity);
    }

    /**
     * Parses the {@link SessionContext} object.
     *
     * @param userIdentity the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.internal.UserIdentity}
     * @throws IOException
     * @throws JsonParseException
     */
    private void parseSessionContext(UserIdentity userIdentity) throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a SessionContext object", jsonParser.getCurrentLocation());
        }

        SessionContext sessionContext = new SessionContext();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
            case "attributes":
                sessionContext.add(CloudTrailEventField.attributes.name(), parseAttributes());
                break;
            case "sessionIssuer":
                sessionContext.add(CloudTrailEventField.sessionIssuer.name(), parseSessionIssuer(sessionContext));
                break;
            case "webIdFederationData":
                sessionContext.add(CloudTrailEventField.webIdFederationData.name(), parseWebIdentitySessionContext(sessionContext));
                break;
            default:
                sessionContext.add(key, parseDefaultValue(key));
                break;
            }
        }

        userIdentity.add(CloudTrailEventField.sessionContext.name(), sessionContext);

    }

    /**
     * Parses the {@link InsightDetails} in CloudTrailEventData
     *
     * @param eventData {@link CloudTrailEventData} needs to parse.
     * @throws IOException
     */
    private void parseInsightDetails(CloudTrailEventData eventData) throws IOException {
        JsonToken nextToken = jsonParser.nextToken();
        if (nextToken == JsonToken.VALUE_NULL) {
            eventData.add(CloudTrailEventField.insightDetails.name(), null);
            return;
        }

        if (nextToken != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a InsightDetails object", jsonParser.getCurrentLocation());
        }

        InsightDetails insightDetails = new InsightDetails();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
                case "eventName":
                    insightDetails.add(CloudTrailEventField.eventName.name(), jsonParser.nextTextValue());
                    break;
                case "eventSource":
                    insightDetails.add(CloudTrailEventField.eventSource.name(), jsonParser.nextTextValue());
                    break;
                case "insightType":
                    insightDetails.add(CloudTrailEventField.insightType.name(), jsonParser.nextTextValue());
                    break;
                case "state":
                    insightDetails.add(CloudTrailEventField.state.name(), jsonParser.nextTextValue());
                    break;
                case "insightContext":
                    this.parseInsightContext(insightDetails);
                    break;
                default:
                    insightDetails.add(key, parseDefaultValue(key));
                    break;
            }
        }
        eventData.add(CloudTrailEventField.insightDetails.name(), insightDetails);
    }

    /**
     * Parses the {@link InsightContext} object.
     *
     * @param insightDetails the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.internal.InsightDetails}
     * @throws IOException
     * @throws JsonParseException
     */
    private void parseInsightContext(InsightDetails insightDetails) throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a InsightContext object", jsonParser.getCurrentLocation());
        }

        InsightContext insightContext = new InsightContext();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
                case "statistics":
                    this.parseInsightStatistics(insightContext);
                    break;
                case "attributions":
                    this.parseInsightAttributionsList(insightContext);
                    break;
                default:
                    insightContext.add(key, parseDefaultValue(key));
            }
        }

        insightDetails.add(CloudTrailEventField.insightContext.name(), insightContext);
    }

    /**
     * Parses the {@link InsightStatistics} object.
     *
     * @param insightContext the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.internal.InsightContext}
     * @throws IOException
     * @throws JsonParseException
     */
    private void parseInsightStatistics(InsightContext insightContext) throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a InsightStatistics object", jsonParser.getCurrentLocation());
        }

        InsightStatistics insightStatistics = new InsightStatistics();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();
            switch (key) {
                case "insightDuration":
                    insightStatistics.add(key, Integer.valueOf(jsonParser.getValueAsInt()));
                    break;
                case "baselineDuration":
                    insightStatistics.add(key, Integer.valueOf(jsonParser.getValueAsInt()));
                    break;
                case "baseline":
                    insightStatistics.add(key, parseAttributesWithDoubleValues());
                    break;
                case "insight":
                    insightStatistics.add(key, parseAttributesWithDoubleValues());
                    break;
                default:
                    insightStatistics.add(key, parseDefaultValue(key));
                    break;
            }

        }

        insightContext.add(CloudTrailEventField.statistics.name(), insightStatistics);
    }

    /**
     * Parses a list of {@link InsightAttributions} objects.
     *
     * @param insightContext the {@link com.amazonaws.services.cloudtrail.processinglibrary.model.internal.InsightContext}
     * @throws IOException
     * @throws JsonParseException
     */
    private void parseInsightAttributionsList(InsightContext insightContext) throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new JsonParseException("Not a InsightAttributions list", jsonParser.getCurrentLocation());
        }

        List<InsightAttributions> insightAttributionsList = new ArrayList<>();

        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            insightAttributionsList.add(parseInsightAttributions());
        }

        insightContext.add(CloudTrailEventField.attributions.name(), insightAttributionsList);
    }

    /**
     * Parses an {@link InsightAttributions} object.
     *
     * @return a single {@link InsightAttributions}
     * @throws IOException
     * @throws JsonParseException
     */
    private InsightAttributions parseInsightAttributions() throws IOException {
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a InsightAttributions object", jsonParser.getCurrentLocation());
        }

        InsightAttributions insightAttributions = new InsightAttributions();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();
            switch (key) {
                case "attribute":
                    insightAttributions.add(CloudTrailEventField.attribute.name(), jsonParser.nextTextValue());
                    break;
                case "baseline":
                    insightAttributions.add(CloudTrailEventField.baseline.name(), parseAttributeValueList());
                    break;
                case "insight":
                    insightAttributions.add(CloudTrailEventField.insight.name(), parseAttributeValueList());
                    break;
                default:
                    insightAttributions.add(key, parseDefaultValue(key));
            }
        }

        return insightAttributions;
    }

    /**
     * Parses a list of {@link AttributeValue} objects.
     *
     * @return list of {@link AttributeValue}
     * @throws IOException
     * @throws JsonParseException
     */
    private List<AttributeValue> parseAttributeValueList() throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new JsonParseException("Not a InsightAttributions list", jsonParser.getCurrentLocation());
        }

        List<AttributeValue> attributeValues = new ArrayList<>();

        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            attributeValues.add(parseAttributeValue());
        }

        return attributeValues;
    }

    /**
     * Parses a single {@link AttributeValue} object.
     *
     * @return a single {@link AttributeValue}, which contains the string value of the attribute, and the average number of
     * occurrences.
     * @throws IOException
     * @throws JsonParseException
     */
    private AttributeValue parseAttributeValue() throws IOException {
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a InsightAttributions object", jsonParser.getCurrentLocation());
        }

        AttributeValue attributeValue = new AttributeValue();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();
            switch (key) {
                case "value":
                    attributeValue.add(CloudTrailEventField.value.name(), jsonParser.nextTextValue());
                    break;
                case "average":
                    attributeValue.add(CloudTrailEventField.average.name(), Double.valueOf(jsonParser.getValueAsDouble()));
                    break;
                default:
                    attributeValue.add(key, parseDefaultValue(key));
            }
        }

        return attributeValue;
    }

    /**
     * Parses the {@link WebIdentitySessionContext} object.
     *
     * @param sessionContext {@link SessionContext}
     * @return the web identity session context
     * @throws IOException
     */
    private WebIdentitySessionContext parseWebIdentitySessionContext(SessionContext sessionContext) throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a WebIdentitySessionContext object", jsonParser.getCurrentLocation());
        }

        WebIdentitySessionContext webIdFederationData = new WebIdentitySessionContext();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
            case "attributes":
                webIdFederationData.add(CloudTrailEventField.attributes.name(), parseAttributes());
                break;
            case "federatedProvider":
                webIdFederationData.add(CloudTrailEventField.federatedProvider.name(), jsonParser.nextTextValue());
                break;
            default:
                webIdFederationData.add(key, parseDefaultValue(key));
                break;
            }
        }

        return webIdFederationData;
    }


    /**
     * Parses the {@link SessionContext} object.
     * This runs only if the session is running with role-based or federated access permissions
     * (in other words, temporary credentials in IAM).
     *
     * @param sessionContext
     * @return the session issuer object.
     * @throws IOException
     */
    private SessionIssuer parseSessionIssuer(SessionContext sessionContext) throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a SessionIssuer object", jsonParser.getCurrentLocation());
        }

        SessionIssuer sessionIssuer = new SessionIssuer();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
            case "type":
                sessionIssuer.add(CloudTrailEventField.type.name(), this.jsonParser.nextTextValue());
                break;
            case "principalId":
                sessionIssuer.add(CloudTrailEventField.principalId.name(), this.jsonParser.nextTextValue());
                break;
            case "arn":
                sessionIssuer.add(CloudTrailEventField.arn.name(), this.jsonParser.nextTextValue());
                break;
            case "accountId":
                sessionIssuer.add(CloudTrailEventField.accountId.name(), this.jsonParser.nextTextValue());
                break;
            case "userName":
                sessionIssuer.add(CloudTrailEventField.userName.name(), this.jsonParser.nextTextValue());
                break;
            default:
                sessionIssuer.add(key, this.parseDefaultValue(key));
                break;
            }
        }

        return sessionIssuer;
    }

    /**
     * Parses the event readOnly attribute.
     *
     * @param eventData
     *
     * @throws JsonParseException
     * @throws IOException
     */
    private void parseReadOnly(CloudTrailEventData eventData) throws IOException {
        jsonParser.nextToken();
        Boolean readOnly = null;
        if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
            readOnly = jsonParser.getBooleanValue();
        }
        eventData.add(CloudTrailEventField.readOnly.name(), readOnly);
    }

    /**
     * Parses the event managementEvent attribute.
     * @param eventData the interesting {@link CloudTrailEventData}
     * @throws IOException
     */
    private void parseManagementEvent(CloudTrailEventData eventData) throws IOException {
        jsonParser.nextToken();
        Boolean managementEvent = null;
        if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
            managementEvent = jsonParser.getBooleanValue();
        }
        eventData.add(CloudTrailEventField.managementEvent.name(), managementEvent);
    }

    /**
     * Parses a list of Resource.
     *
     * @param eventData the resources belong to
     * @throws IOException
     */
    private void parseResources(CloudTrailEventData eventData) throws IOException {
        JsonToken nextToken = jsonParser.nextToken();
        if (nextToken == JsonToken.VALUE_NULL) {
            eventData.add(CloudTrailEventField.resources.name(), null);
            return;
        }

        if (nextToken != JsonToken.START_ARRAY) {
            throw new JsonParseException("Not a list of resources object", jsonParser.getCurrentLocation());
        }

        List<Resource> resources = new ArrayList<Resource>();

        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            resources.add(parseResource());
        }

        eventData.add(CloudTrailEventField.resources.name(), resources);
    }

    /**
     * Parses a single Resource.
     *
     * @return a single resource
     * @throws IOException
     */
    private Resource parseResource() throws IOException {
        //current token is ready consumed by parseResources
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a Resource object", jsonParser.getCurrentLocation());
        }

        Resource resource = new Resource();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
            default:
                resource.add(key, parseDefaultValue(key));
                break;
            }
        }

        return resource;
    }


    /**
     * Parses the {@link Addendum} in CloudTrailEventData
     *
     * @param eventData {@link CloudTrailEventData} must parse.
     * @throws IOException
     */
    private void parseAddendum(CloudTrailEventData eventData) throws IOException {
        JsonToken nextToken = jsonParser.nextToken();
        if (nextToken == JsonToken.VALUE_NULL) {
            eventData.add(CloudTrailEventField.addendum.name(), null);
            return;
        }

        if (nextToken != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not an Addendum object", jsonParser.getCurrentLocation());
        }

        Addendum addendum = new Addendum();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
                case "reason":
                    addendum.add(CloudTrailEventField.reason.name(), jsonParser.nextTextValue());
                    break;
                case "updatedFields":
                    addendum.add(CloudTrailEventField.updatedFields.name(), jsonParser.nextTextValue());
                    break;
                case "originalRequestID":
                    addendum.add(CloudTrailEventField.originalRequestID.name(), jsonParser.nextTextValue());
                    break;
                case "originalEventID":
                    addendum.add(CloudTrailEventField.originalEventID.name(), jsonParser.nextTextValue());
                    break;
                default:
                    addendum.add(key, parseDefaultValue(key));
                    break;
            }
        }
        eventData.add(CloudTrailEventField.addendum.name(), addendum);
    }

    private void parseTlsDetails(CloudTrailEventData eventData) throws IOException {
        JsonToken nextToken = jsonParser.nextToken();
        if (nextToken == JsonToken.VALUE_NULL) {
            eventData.add(CloudTrailEventField.tlsDetails.name(), null);
            return;
        }

        if (nextToken != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a TLS Details object", jsonParser.getCurrentLocation());
        }

        TlsDetails tlsDetails = new TlsDetails();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();

            switch (key) {
                case "tlsVersion":
                    tlsDetails.add(CloudTrailEventField.tlsVersion.name(), jsonParser.nextTextValue());
                    break;
                case "cipherSuite":
                    tlsDetails.add(CloudTrailEventField.cipherSuite.name(), jsonParser.nextTextValue());
                    break;
                case "clientProvidedHostHeader":
                    tlsDetails.add(CloudTrailEventField.clientProvidedHostHeader.name(), jsonParser.nextTextValue());
                    break;
                default:
                    tlsDetails.add(key, this.parseDefaultValue(key));
                    break;
            }
        }

        eventData.add(CloudTrailEventField.tlsDetails.name(), tlsDetails);
    }

    /**
     * Parses the event with key as default value.
     *
     * If the value is JSON null, then we will return null.
     * If the value is JSON object (of starting with START_ARRAY or START_OBject) , then we will convert the object to String.
     * If the value is JSON scalar value (non-structured object), then we will return simply return it as String.
     *
     * @param key
     * @throws IOException
     */
    private String parseDefaultValue(String key) throws IOException {
        jsonParser.nextToken();
        String value = null;
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (currentToken != JsonToken.VALUE_NULL) {
            if (currentToken == JsonToken.START_ARRAY || currentToken == JsonToken.START_OBJECT) {
                JsonNode node = jsonParser.readValueAsTree();
                value = node.toString();
            } else {
                value = jsonParser.getValueAsString();
            }
        }
        return value;
    }

    /**
     * Parses attributes as a Map, used in both parseWebIdentitySessionContext and parseSessionContext
     *
     * @return attributes for either session context or web identity session context
     * @throws IOException
     */
    private Map<String, String> parseAttributes() throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not a Attributes object", jsonParser.getCurrentLocation());
        }

        Map<String, String> attributes = new HashMap<>();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();
            String value = jsonParser.nextTextValue();
            attributes.put(key, value);
        }

        return attributes;
    }

    /**
     * Parses attributes as a Map<String, Double>, used to parse InsightStatistics
     *
     * @return attributes for insight statistics
     * @throws IOException
     */
    private Map<String, Double> parseAttributesWithDoubleValues() throws IOException {
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException("Not an Attributes object", jsonParser.getCurrentLocation());
        }

        Map<String, Double> attributes = new HashMap<>();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();
            Double value = jsonParser.getValueAsDouble();
            attributes.put(key, value);
        }

        return attributes;
    }

    /**
     * This method convert a String to UUID type. Currently EventID is in UUID type.
     *
     * @param str that need to convert to UUID
     * @return the UUID.
     */
    private UUID convertToUUID(String str) {
        return UUID.fromString(str);
    }

    /**
     * This method convert a String to Date type. When parse error happened return current date.
     *
     * @param dateInString the String to convert to Date
     * @return Date the date and time in coordinated universal time
     * @throws IOException
     */
    private Date convertToDate(String dateInString) throws IOException {
        Date date = null;
        if (dateInString != null) {
            try {
                date = LibraryUtils.getUtcSdf().parse(dateInString);
            } catch (ParseException e) {
                throw new IOException("Cannot parse " + dateInString + " as Date", e);
            }
        }
        return date;
    }
}
