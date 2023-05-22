package com.amazonaws.services.cloudtrail.processinglibrary.model.internal;

import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.CloudTrailDataStore;
import com.amazonaws.services.cloudtrail.processinglibrary.model.internal.CloudTrailEventField;

public class OnBehalfOf extends CloudTrailDataStore {

    public String getUserId() {
        return (String) this.get(CloudTrailEventField.onBehalfOfUserId.name());
    }

    public String getIdentityStoreArn() {
        return (String) this.get(CloudTrailEventField.onBehalfOfIdentityStoreArn.name());
    }
}