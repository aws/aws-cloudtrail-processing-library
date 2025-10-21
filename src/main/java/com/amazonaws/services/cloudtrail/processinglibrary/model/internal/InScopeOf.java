package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

public class InScopeOf extends CloudTrailDataStore {
    public String getSourceAccount() {
        return (String) this.get(CloudTrailEventField.inScopeOfSourceAccount.name());
    }

    public String getSourceArn() {
        return (String) this.get(CloudTrailEventField.inScopeOfSourceArn.name());
    }

    public String getIssuerType() {
        return (String) this.get(CloudTrailEventField.inScopeOfIssuerType.name());
    }

    public String getCredentialsIssuedTo() {
        return (String) this.get(CloudTrailEventField.inScopeOfCredentialsIssuedTo.name());
    }
}
