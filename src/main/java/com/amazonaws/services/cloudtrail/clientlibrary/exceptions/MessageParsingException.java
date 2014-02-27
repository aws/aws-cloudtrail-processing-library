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
package com.amazonaws.services.cloudtrail.clientlibrary.exceptions;

import com.amazonaws.services.sqs.model.Message;

/**
 * 
 */
public class MessageParsingException extends ClientLibraryException{
	
	private static final long serialVersionUID = 4897982280661576050L;
	private Message sqsMessage;
	
	public MessageParsingException(String message, Message sqsMessage) {
		super(message);
		this.sqsMessage = sqsMessage;
	}
	
	public MessageParsingException(String message, Exception e, Message sqsMessage) {
		super(message, e);
		this.sqsMessage = sqsMessage;
	}

	/**
	 * @return the sqsMessage
	 */
	public Message getSqsMessage() {
		return sqsMessage;
	}
	
}
