package com.rspsi.util;

import java.io.File;

import com.google.common.primitives.Ints;

public class FileUtils {
	
	public static boolean isMapFile(File file) {
		return isPackFile(file) || isDatFile(file) || isGzFile(file);
	}
	
	public static boolean isPackFile(File file) {
		return file.getName().toLowerCase().endsWith(".pack");
	}
	
	public static boolean isDatFile(File file) {
		return file.getName().toLowerCase().endsWith(".dat");
	}
	
	public static boolean isGzFile(File file) {
		return file.getName().toLowerCase().endsWith(".gz");
	}

	public static boolean isDatOrGzFile(File file) {
		return isDatFile(file) || isGzFile(file);
	}

	public static int getNameAsInteger(File file) {
		return Ints.tryParse(file.getName().toLowerCase().replace(".gz", "").replace(".dat", "").trim());
	}
	
	
}
