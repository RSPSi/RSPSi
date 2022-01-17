package com.rspsi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.rspsi.core.misc.JsonUtil;

public class Settings {

	//public static Properties properties = new Properties();
	
	public static Map<String, Object> properties = Maps.newConcurrentMap();
	
	public static void saveSettings() {
		Path rootDir = Paths.get(System.getProperty("user.home"), ".rspsi");
		File f = new File(rootDir.toFile(), "settings.json");
		if(!rootDir.toFile().exists())
			rootDir.toFile().mkdirs();
		if(!f.exists())
			try {
				f.createNewFile();
				properties.put("shutdown", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		ObjectMapper mapper = JsonUtil.getDefaultMapper();
		try {
			mapper.writeValue(f, properties);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void loadSettings() {
		loadSettingsOld();
		File f = new File(Paths.get(System.getProperty("user.home"), ".rspsi").toFile(), "settings.json");
		if(!f.exists())
			return;
		ObjectMapper mapper = JsonUtil.getDefaultMapper();
		// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			properties.putAll(mapper.readValue(f, new TypeReference<Map<String, Object>>() {
			}));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void loadSettingsOld() {
		File f = new File(Paths.get(System.getProperty("user.home"), ".rspsi").toFile(), "settings.conf");
		if(!f.exists())
			return;
		try(FileInputStream fis = new FileInputStream(f)) {
			Properties propertiesOld = new Properties();
			propertiesOld.load(fis);
			for(Entry<Object, Object> entry : propertiesOld.entrySet()) {
				try {
					properties.put((String) entry.getKey(), entry.getValue());
				} catch(Exception ex) {
					//Key invalid?
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		f.delete();
	}

	public static <T> T getSetting(String key, T defaultValue) {
		if(properties.get(key) != null) {
			if(properties.get(key).getClass().isAssignableFrom(defaultValue.getClass()))
				return (T) properties.get(key);
		}
		return defaultValue;
	}


	public static <T> void putSetting(String key, T value) {
		properties.put(key, value);
		saveSettings();
	}

	public static void resetSettings() {
		properties.clear();
		saveSettings();
	}

	public static void clearSetting(String key) {
		properties.remove(key);
		saveSettings();
	}
}
