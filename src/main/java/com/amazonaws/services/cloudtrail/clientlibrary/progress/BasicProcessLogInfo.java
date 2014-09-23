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
package com.amazonaws.services.cloudtrail.clientlibrary.progress;

import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailSource;

public class BasicProcessLogInfo implements ProgressLogInfo, ProgressSourceInfo {

    private boolean isSuccess;
    private CloudTrailLog log;
    private CloudTrailSource source;
    
    public BasicProcessLogInfo(CloudTrailSource source, CloudTrailLog log, boolean isSuccess) {
        this.source = source;
        this.log = log;
        this.isSuccess = isSuccess;
    }
    
    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    @Override
    public CloudTrailLog getLog() {
        return this.log;
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
        if (log != null) {
            builder.append("log: ");
            builder.append(log);
            builder.append(", ");
        }
        if (source != null) {
            builder.append("source: ");
            builder.append(source);
        }
        builder.append("}");
        return builder.toString();
    }
}
