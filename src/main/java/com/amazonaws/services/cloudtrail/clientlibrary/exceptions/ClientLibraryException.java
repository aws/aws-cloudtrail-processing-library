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

/**
 * This exception can be thrown when calling user's implemented call back function.
 *
 */
public class ClientLibraryException extends Exception {
	
	private static final long serialVersionUID = 8757412348402829171L;
	
	public ClientLibraryException(String message) {
		super(message);
	}
	
	public ClientLibraryException(String message, Exception e) {
		super(message, e);
	}
}