package org.displee.util;

import com.displee.io.impl.InputBuffer;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

public class GZIPUtils {

	public static byte[] gzipBytes(byte[] input) {
		 try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
				 GzipCompressorOutputStream gzipper = new GzipCompressorOutputStream(bout))
	        {
		
	            gzipper.write(input, 0, input.length);
	            gzipper.close();

	            return bout.toByteArray();
	        } catch(Exception ex) {
	        	return null;
	        }
	}

	public static byte[] unzip(byte[] input)  {
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
	        } catch(Exception ex) {
	        	return null;
	        }
	}
	

	private static Inflater inflater;
	public static boolean inflate(InputBuffer inputStream, byte[] data) {
		try {
			if ((inputStream.get(inputStream.getOffset()) ^ 0xffffffff) != -32 || inputStream.get(inputStream.getOffset() + 1) != -117) {
				return false;
			}
			if (inflater == null) {
				inflater = new Inflater(true);
			}
			try {
				inflater.setInput(inputStream.array(), 10 + inputStream.getOffset(), (-10 - inputStream.getOffset() - (8 - inputStream.raw().length)));
				inflater.inflate(data);
			} catch (Exception exception) {
				inflater.reset();
				return false;
			}
			inflater.reset();
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}
		return true;
	}

}

