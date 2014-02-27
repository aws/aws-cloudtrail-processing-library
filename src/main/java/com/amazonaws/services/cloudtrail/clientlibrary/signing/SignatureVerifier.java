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
package com.amazonaws.services.cloudtrail.clientlibrary.signing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.encoders.Base64;

import com.amazonaws.services.cloudtrail.clientlibrary.model.SignatureVerificationName;
import com.amazonaws.services.cloudtrail.clientlibrary.model.CloudTrailLog;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.ClientLibraryUtils;
import com.amazonaws.services.cloudtrail.clientlibrary.utils.S3Manager;
 
public class SignatureVerifier {
    
    private static final Log logger = LogFactory.getLog(SignatureVerifier.class);

    /**
     * add security provider.
     */
    static {
    	Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * BouncyCastle as JCA provider 
     */
    public static final String CRYPTO_PROVIDER = "BC"; 

    /**
     *  Digital signing algorithm 
     */
    public static final String SIGNING_ALGORITHM = "SHA256withRSA";
    
    /**
     * The S3 bucket where the certificate file stored
     */
//    public static final String CERT_BUCKET = "awscloudtrail";
    public static final String CERT_BUCKET = "awscloudtrail-non-prod"; //TODO; remove it before release
    
    
	public static void verifyLogFile(byte[] signedS3ObjectBytes, Map<String, String> s3ObjectMetadata, CloudTrailLog source, S3Manager s3Manager) {
		logger.info("Verify log file " + source.getS3ObjectKey() + " from bucket " + source.getS3Bucket());
		
		String signature = s3ObjectMetadata.get("signature");
		String certificatePath = s3ObjectMetadata.get("certificates3path");
		
		try {
			// download and verify Certificate from AWS CloudTrail
			InputStream certificateStream = s3Manager.getObject(CERT_BUCKET, certificatePath).getObjectContent();
			X509Certificate certificate = pemCertFromInputStream(certificateStream);
			certificateStream.close();
			verifyCertSignature(certificate);
			
			// download and verify Crl from AWS CloudTrail
			String[] crlHttpPath = getCrlDistributionPoints(certificate);			
			InputStream crlStream = s3Manager.getObject(crlHttpPath[0], crlHttpPath[1]).getObjectContent();
			X509CRL crl = pemCrlFromInputStream(crlStream);
			crlStream.close();
			verifyCrlSignature(crl, certificate);
			
			// check if certificate is valid.
			verifyCertValidty(crl, certificate, source);
			
			// certificate and signature are all good, now verify CloudTrail log file.
//			byte[] signedS3ObjectBytes = toByteArray(signedS3Object.getObjectContent());
			boolean verified =  verifySignature(signedS3ObjectBytes, certificate, Base64.decode(signature));
			
			// update signature verification information in AWSCloudTrailSource
			if (verified) {
				source.setSignatureVerification(SignatureVerificationName.ValidSignature);
			} else {
				source.setSignatureVerification(SignatureVerificationName.InvalidSignature);
			}
			
		} catch (Exception e) {
			source.setSignatureVerification(SignatureVerificationName.SignatureNotVerified);
			logger.error("Error when verify CloudTrail logs " + e.getMessage(), e);
		}
	}
	
    /**
     * Verify Certificate signature.
     * 
     * @param cert
     * @throws InvalidKeyException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     */
    private static void verifyCertSignature(X509Certificate cert)
            throws InvalidKeyException, CertificateException,
            NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
    	cert.verify(cert.getPublicKey(), CRYPTO_PROVIDER);
    }

    /**
     * Verify certificate revoke list signature.
     * @param crl
     * @param cert
     * @throws InvalidKeyException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws CRLException
     */
    private static void verifyCrlSignature(X509CRL crl, X509Certificate cert)
            throws InvalidKeyException, CertificateException,
            NoSuchAlgorithmException, NoSuchProviderException, SignatureException, CRLException {
    	crl.verify(cert.getPublicKey(), CRYPTO_PROVIDER);
    }  
    
    /**
     * Verify certificate validity
     * 
     * @param crl
     * @param cert
     */
    private static void verifyCertValidty(X509CRL crl, X509Certificate cert, CloudTrailLog source) {
    	// check whether certificate is revoked 
		X509CRLEntry crlEntry = crl.getRevokedCertificate(cert.getSerialNumber());
		if (crlEntry != null) {
			source.setSignatureVerification(SignatureVerificationName.RevokedCertificate);
			logger.warn("Certificate " + cert.getSerialNumber() + " is revoked due to " + 
						crlEntry.getRevocationReason() + " on " + crlEntry.getRevocationDate());
		}

		// check whether certificate is expired 
		if (cert.getNotAfter().compareTo(new Date()) < 0) {
			source.setSignatureVerification(SignatureVerificationName.ExpiredCertificate);
			logger.warn("Certificate " + cert.getSerialNumber() + " is expired at " + cert.getNotAfter());
		}
    }

    /**
     * Verify CloudTrail log signature.
     * 
     * @param signedData
     * @param cert
     * @param signature
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private static boolean verifySignature(byte[] signedData, Certificate cert, byte[] signature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signer = Signature.getInstance(SIGNING_ALGORITHM);
        signer.initVerify(cert);
        signer.update(signedData);
        return signer.verify(signature);
    }
    
    /**
     * Read pem encoded x509 certificate from input stream
     *
     * @param inputStream
     * @return X509Certificate
     * @throws IOException
     * @throws CertificateException
     */
    private static X509Certificate pemCertFromInputStream(InputStream inputStream)
            throws IOException, CertificateException {

        Object pemObj = readPemObject(new InputStreamReader(inputStream));

        return convertToX509Certificate(pemObj);
    }

    /**
     * Read pem encoded crl from input stream
     *
     * @param inputStream
     * @return X509CRL
     * @throws IOException
     * @throws CRLException
     */
    private static X509CRL pemCrlFromInputStream(InputStream inputStream) throws IOException, CRLException {

        Object crlObj = readPemObject(new InputStreamReader(inputStream));

        return convertToX509CRL(crlObj);
    }

    /**
     * Help method to read pem encoded object
     *
     * @param reader
     * @return pem object
     * @throws IOException
     */
    private static Object readPemObject(Reader reader) throws IOException {
        PEMParser parser = new PEMParser(reader);
        Object pemObj = parser.readObject();
        parser.close();
        return pemObj;
    }

    /**
     * Help method to convert pem encoded certificate to X509Certificate
     *
     * @param certObj
     * @return X509Certificate
     * @throws CertificateException
     */
    private static X509Certificate convertToX509Certificate(Object certObj) throws CertificateException {
		return new JcaX509CertificateConverter().setProvider(CRYPTO_PROVIDER)
				.getCertificate((X509CertificateHolder) certObj);
    }

    /**
     * Help method to convert pem encoded CRL to X509CRL
     *
     * @param crlObj
     * @return X509CRL
     * @throws CRLException
     */
    private static X509CRL convertToX509CRL(Object  crlObj) throws CRLException {
        return new JcaX509CRLConverter().setProvider(CRYPTO_PROVIDER).getCRL((X509CRLHolder) crlObj);
    }
    
    /**
     * Extract crl ditribution points from certificate. 
     * Only one certificate available. 
     * 
     * @param cert
     * @return String Array. First String is bucket name; second String is objectKey.
     * @throws IOException
     */
    private static String[] getCrlDistributionPoints(X509Certificate cert) throws IOException {
        List<String> crlUrls = new ArrayList<String>();
        byte[] crlDistPiontByteArray = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (crlDistPiontByteArray == null) {
        	throw new IllegalStateException("Crl distribution points is null");
        }
        ASN1InputStream crlDistPointAsn1InStream = new ASN1InputStream(new ByteArrayInputStream(crlDistPiontByteArray));
        ASN1Primitive crlAsn1Primitive;

        try {
            crlAsn1Primitive = crlDistPointAsn1InStream.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            crlDistPointAsn1InStream.close();
        }

        if (crlAsn1Primitive instanceof DEROctetString) {
            DEROctetString crlDistPointOct = (DEROctetString) crlAsn1Primitive;

            CRLDistPoint crlDistPiont = CRLDistPoint.getInstance(crlDistPointOct.getOctets());

            for (DistributionPoint distPoint : crlDistPiont.getDistributionPoints()) {
                DistributionPointName dpn = distPoint.getDistributionPoint();
                if (dpn != null && dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                    for (int j = 0; j < genNames.length; j++) {
                        if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            String url = DERIA5String.getInstance(genNames[j].getName()).getString();
                            crlUrls.add(url);
                        }
                    }
                }
            }
        } else {
        	throw new RuntimeException("It is not a DEROctetString instance");
        }
        
		if (crlUrls.size() != 1) {
			throw new RuntimeException("Certificate Revocation List should only have one Crl");
		}
		
        return ClientLibraryUtils.toBucketNameObjectKey(crlUrls.get(0));
    }
}