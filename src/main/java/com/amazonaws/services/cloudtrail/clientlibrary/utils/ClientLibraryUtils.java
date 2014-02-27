package com.amazonaws.services.cloudtrail.clientlibrary.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClientLibraryUtils {
    /**
     * Convenient function to convert InputSteam to byte array.
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	int nRead = 0;
    	byte[] bytes = new byte[1024];
    	while ((nRead = inputStream.read(bytes, 0, 1024)) != -1) {
    		buffer.write(bytes, 0, nRead);
    	}
    	return buffer.toByteArray();
    }
    
    /**
     * Split a http representation of s3 url to bucket name and object key. Example:
     * input: s3ObjectHttpUrl = http://s3-us-west-2.amazonaws.com/mybucket/myobjectpath1/myobjectpath2/myobject.extension
     * output: {"mybucket", "myobjectpath1/myobjectpath2/myobject.extension"}
     * 
     * @param s3ObjectHttpUrl
     * @return
     */
    public static String[] toBucketNameObjectKey(String s3ObjectHttpUrl) {
        if (s3ObjectHttpUrl == null) {
            return null;
        }

        int start = s3ObjectHttpUrl.indexOf(".amazonaws.com/");
        int length = s3ObjectHttpUrl.length();
        
        if (start != -1) {
        	String bucketNameAndObjectKey = s3ObjectHttpUrl.substring(start + ".amazonaws.com/".length(), length);
        	return bucketNameAndObjectKey.split("/", 2);
        }
        return null;
        
    }
}
