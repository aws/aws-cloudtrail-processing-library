package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;


public class InvokedByDelegate extends CloudTrailDataStore {

    public String getAccountId() {
        return (String) this.get(CloudTrailEventField.invokedByDelegateAccountId.name());
    }
}
