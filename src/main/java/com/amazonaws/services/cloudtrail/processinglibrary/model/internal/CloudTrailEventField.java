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
 * Internal use only.
 */
public enum CloudTrailEventField {
    Records,
    accessKeyId,
    accountId,
    addendum,
    additionalEventData,
    annotation,
    apiVersion,
    arn,
    ARN,
    ARNPrefix,
    attribute,
    attributes,
    attributions,
    average,
    awsRegion,
    baseline,
    baselineDuration,
    cipherSuite,
    clientProvidedHostHeader,
    credentialId,
    credentialsIssuedTo,
    deviceFamily,
    deviceId,
    edgeDeviceDetails,
    errorCode,
    errorMessage,
    eventCategory,
    eventID,
    eventName,
    eventSource,
    eventTime,
    eventType,
    eventVersion,
    federatedProvider,
    identityProvider,
    issuerType,
    inScopeOf,
    insight,
    insightContext,
    insightDetails,
    insightDuration,
    insightType,
    invokedBy,
    managementEvent,
    originalRequestID,
    originalEventID,
    onBehalfOf,
    onBehalfOfUserId,
    onBehalfOfIdentityStoreArn,
    principalId,
    readOnly,
    reason,
    recipientAccountId,
    requestID,
    requestParameters,
    resources,
    responseElements,
    serviceEventDetails,
    sessionContext,
    sessionCredentialFromConsole,
    sessionIssuer,
    sharedEventID,
    snowJobId,
    sourceIPAddress,
    sourceAccount,
    sourceArn,
    state,
    statistics,
    tlsDetails,
    tlsVersion,
    type,
    updatedFields,
    userAgent,
    userIdentity,
    userName,
    value,
    vpcEndpointAccountId,
    vpcEndpointId,
    webIdFederationData
}
