package com.amazonaws.services.cloudtrail.clientlibrary.model;

public enum SignatureVerificationName {
	
	/**
	 * AWS CloudTrail log file is not verified.
	 */
	SignatureNotVerified,
	
	/**
	 * AWS CloudTrail log file verified and it is valid.
	 */
	ValidSignature, 
	
	/**
	 * AWS CloudTrail log file verified and it is invalid.
	 */
	InvalidSignature,
	
	/**
	 * Certificate attached to AWS CloudTrail log file is revoked. 
	 */
	RevokedCertificate,
	
	/**
	 * Certificate attached to AWS CloudTrail log file is expired. 
	 */
	ExpiredCertificate
}
