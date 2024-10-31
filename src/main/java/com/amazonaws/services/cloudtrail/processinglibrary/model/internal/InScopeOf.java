package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

public class InScopeOf extends CloudTrailDataStore {
    public String getSourceAccount() {
        return (String) this.get(CloudTrailEventField.sourceAccount.name());
    }

    public String getSourceArn() {
        return (String) this.get(CloudTrailEventField.sourceArn.name());
    }

    public String getIssuerType() {
        return (String) this.get(CloudTrailEventField.issuerType.name());
    }

    public String getCredentialsIssuedTo() {
        return (String) this.get(CloudTrailEventField.credentialsIssuedTo.name());
    }
}
