/*******************************************************************************
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.amazonaws.services.cloudtrail.processinglibrary.progress;

import com.amazonaws.services.cloudtrail.processinglibrary.model.CloudTrailSource;

public class BasicProcessSourceInfo implements ProgressSourceInfo{
    private boolean isSuccess;
    private CloudTrailSource source;

    public BasicProcessSourceInfo(CloudTrailSource source, boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
        this.source = source;
    }

    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    @Override
    public CloudTrailSource getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{isSuccess: ");
        builder.append(isSuccess);
        builder.append(", ");
        if (source != null) {
            builder.append("source: ");
            builder.append(source);
        }
        builder.append("}");
        return builder.toString();
    }
}
