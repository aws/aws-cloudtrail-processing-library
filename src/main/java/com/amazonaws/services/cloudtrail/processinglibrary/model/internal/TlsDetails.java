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
 * Information about the Transport Layer Security (TLS) details of a service API call.
 */
public class TlsDetails extends CloudTrailDataStore {

    /**
     * Get the TLS version of the request.
     *
     * @return The TLS version
     */
    public String getTlsVersion() {
        return (String) this.get(CloudTrailEventField.tlsVersion.name());
    }

    /**
     * Get the cipher suite (combination of security algorithms used) of the request.
     *
     * @return The cipher suite
     */
    public String getCipherSuite() {
        return (String) this.get(CloudTrailEventField.cipherSuite.name());
    }

    /**
     * Get the client-provided host header.
     *
     * @return the FQDN of the client that made the request.
     */
    public String getClientProvidedHostHeader() {
        return (String) this.get(CloudTrailEventField.clientProvidedHostHeader.name());
    }
}
