package com.jagex.util;

public final class StringUtils {

	private static final char[] BASE_37_CHARACTERS = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };

	public static String decodeBase37(long encoded) {
		if (encoded <= 0 || encoded >= 0x5b5b57f8a98a5dd1L || encoded % 37 == 0L)
			return "invalid_name";

		int length = 0;
		char[] chars = new char[12];
		while (encoded != 0) {
			long name = encoded;
			encoded /= 37L;
			chars[11 - length++] = BASE_37_CHARACTERS[(int) (name - encoded * 37)];
		}

		return new String(chars, 12 - length, length);
	}

	public static String decodeIp(int ip) {
		return (ip >> 24 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip & 0xff);
	}

	public static long encodeBase37(String string) {
		long encoded = 0;

		for (int index = 0; index < string.length() && index < 12; index++) {
			char character = string.charAt(index);
			encoded *= 37;

			if (character >= 'A' && character <= 'Z') {
				encoded += character - 'A' + 1;
			} else if (character >= 'a' && character <= 'z') {
				encoded += character - 'a' + 1;
			} else if (character >= '0' && character <= '9') {
				encoded += character - '0' + 26 + 1;
			}
		}

		while (encoded % 37 == 0 && encoded != 0) {
			encoded /= 37;
		}
		return encoded;
	}

	public static String format(String string) {
		if (string.length() > 0) {
			char[] chars = string.toCharArray();
			for (int index = 0; index < chars.length; index++) {
				if (chars[index] == '_') {
					chars[index] = ' ';

					if (index + 1 < chars.length && chars[index + 1] >= 'a' && chars[index + 1] <= 'z') {
						chars[index + 1] = (char) (chars[index + 1] - 32);
					}
				}
			}

			if (chars[0] >= 'a' && chars[0] <= 'z') {
				chars[0] = (char) (chars[0] - 32);
			}
			return new String(chars);
		}

		return string;
	}

	public static String getAsterisks(String string) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			buffer.append("*");
		}

		return buffer.toString();
	}

	public static long hashSpriteName(String name) {
		name = name.toUpperCase();
		long hash = 0;
		for (int index = 0; index < name.length(); index++) {
			hash = hash * 61 + name.charAt(index) - 32;
			hash = hash + (hash >> 56) & 0xFFFFFFFFFFFFFFL;
		}

		return hash;
	}

}