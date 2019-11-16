package com.rspsi.options;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Config {
	
	public static final int HEIGHT_ADJUST = 2;
	public static final boolean HEIGHT_FALLOFF = false;
	public static final boolean HEIGHT_SMOOTHING = false;
	public static final boolean ALL_HEIGHTS_MANUAL = true;
	
	
	public static final int MINIMAP_SIZE = 256;
	

	public static StringProperty cacheLocation = new SimpleStringProperty("cache" + File.separator);

	public static final boolean LOAD_ANIMS = true;

}
