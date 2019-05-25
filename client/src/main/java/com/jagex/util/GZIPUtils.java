package com.jagex.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class GZIPUtils {

	public static byte[] gzipBytes(byte[] input) throws IOException {
		 try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
				 GzipCompressorOutputStream gzipper = new GzipCompressorOutputStream(bout))
	        {
		
	            gzipper.write(input, 0, input.length);
	            gzipper.close();

	            return bout.toByteArray();
	        }
	}

	public static byte[] unzip(byte[] input) throws IOException  {
		  try (ByteArrayInputStream bin = new ByteArrayInputStream(input);
				  GzipCompressorInputStream gzipper = new GzipCompressorInputStream(bin))
	        {
			  ByteArrayOutputStream out = new ByteArrayOutputStream();
			  final byte[] buffer = new byte[2048];
			    int n = 0;
			    while (-1 != (n = gzipper.read(buffer))) {
			        out.write(buffer, 0, n);
			    }
			    bin.close();
			    gzipper.close();
			  return out.toByteArray();
	        }
	}

}
