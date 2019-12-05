package org.displee.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class containing utilities that are used in this cache library.
 * @author Displee
 */
public class Miscellaneous {



	/**
	 * Copies all bytes from the input stream to the output stream. Does not close or flush either
	 * stream.
	 *
	 * @param from the input stream to read from
	 * @param to the output stream to write to
	 * @return the number of bytes copied
	 */
	public static long copy(InputStream from, OutputStream to) throws IOException {
		byte[] buf = new byte[0x1000];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}

	public static int to317Hash(String name) {
		int hash = 0;
		name = name.toUpperCase();
		for (int i = 0; i < name.length(); i++) {
			hash = (hash * 61 + name.charAt(i)) - 32;
		}
		return hash;
	}

	public static int getConfigArchive(int id, int bits) {
		return (id) >> bits;
	}

	public static int getConfigFile(int id, int bits) {
		return (id) & (1 << bits) - 1;
	}

}