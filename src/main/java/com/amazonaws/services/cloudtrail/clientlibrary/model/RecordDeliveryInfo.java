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
package com.amazonaws.services.cloudtrail.clientlibrary.model;

public class RecordDeliveryInfo {
	
	private CloudTrailLog source;
	private long charStart;
	private long charEnd;
	
	public RecordDeliveryInfo(CloudTrailLog source, long charStart, long charEnd) {
		super();
		this.source = source;
		this.charStart = charStart;
		this.charEnd = charEnd;
	}
	
	/**
	 * @return the source
	 */
	public CloudTrailLog getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(CloudTrailLog source) {
		this.source = source;
	}

	/**
	 * @return the charStart
	 */
	public long getCharStart() {
		return charStart;
	}

	/**
	 * @param charStart the charStart to set
	 */
	public void setCharStart(long charStart) {
		this.charStart = charStart;
	}

	/**
	 * @return the charEnd
	 */
	public long getCharEnd() {
		return charEnd;
	}

	/**
	 * @param charEnd the charEnd to set
	 */
	public void setCharEnd(long charEnd) {
		this.charEnd = charEnd;
	}

	/**
	 * Converts this AWSCloudTrailRecordDeliveryInfo object to a String of the form.
	 */
	@Override
	public String toString() {
		return "AWSCloudTrailRecordDeliveryInfo [source=" + source
				+ ", charStart=" + charStart + ", charEnd=" + charEnd + "]";
	}

	/** 
	 * 	 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (charEnd ^ (charEnd >>> 32));
		result = prime * result + (int) (charStart ^ (charStart >>> 32));
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordDeliveryInfo other = (RecordDeliveryInfo) obj;
		if (charEnd != other.charEnd)
			return false;
		if (charStart != other.charStart)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}


}
