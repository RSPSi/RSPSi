package com.rspsi.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rspsi.ui.misc.NamedValueObject;

public class ReflectionUtil {

	public static List<NamedValueObject> getValueAsNamedValueList(Object object) {
		Map<String, String> values = ReflectionUtil.getValues(object);

		List<NamedValueObject> namedValueList = new ArrayList<>();
		for (Entry<String, String> entry : values.entrySet()) {
			//System.out.println(entry.getKey() + ": " + entry.getValue());
			namedValueList.add(new NamedValueObject(entry.getKey(), entry.getValue()));
		}

		return namedValueList;
	}
	
	public static void printValues(Object obj) {
		Map<String, String> map = getValues(obj);
		System.out.println(map.size());
		for (Entry<String, String> entry : map.entrySet()) {
			System.out.println(obj.getClass().getName() + "." + entry.getKey() + ":" + entry.getValue());
		}
	}

	public static Map<String, String> getValues(Object object) {
		Map<String, String> values = new HashMap<>();
		for (Field field : object.getClass().getDeclaredFields()) {
			if (field == null || Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			String name = field.getName();
			Object value = null;
			try {
				field.setAccessible(true);
				value = field.get(object);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				 e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				 e.printStackTrace();
			}
			if (value != null && value instanceof String[]) {
				String[] val = (String[]) value;
				String s = "{";
				if (val.length > 0) {
					for (String element : val)
						if (element != null) {
							s += element + ", ";
						}
				}
				if(s.length() > 1)
					s = s.substring(0, s.length() - 2);
				
					s += "}";
				value = s;
			} else if (value != null && value.getClass().isArray() && value.getClass().isAssignableFrom(int[].class)) {
				int[] val = (int[]) value;
				String s = "{";
				if (val.length > 0) {
					for (int element : val) {
						s += element + ", ";
					}

				}
				if (s.length() > 1) {
					s = s.substring(0, s.length() - 2);
				}
				s += "}";
				value = s;
			} else if (value != null && value.getClass().isArray() && value instanceof int[][]) {
				int[][] val = (int[][]) value;
				if (val.length > 0) {
					String s = "{";
					for (int[] element : val)
						if (element != null && element.length > 0) {
							String s2 = "{";
							for (int i2 = 0; i2 < element.length; i2++) {
								// if(val[i] != null)
								s2 += element[i2] + ", ";
							}

							if (s2.length() > 1) {
								s2 = s2.substring(0, s2.length() - 2);
							}
							s2 += "}";
							s += s2;
						}

					if (s.length() > 1) {
						s = s.substring(0, s.length() - 2);
					}
					s += "}";
					value = s;
				}
			} else if (value != null && value.getClass().isArray() && value instanceof byte[]) {
				byte[] val = (byte[]) value;
				String s = "";
				if (val.length > 0) {
					for (byte element : val) {
						s += element + ", ";
					}

				}
				s = s.substring(0, s.length() - 2) + "}";
				value = s;
			} else if (value != null && value.getClass().isArray() && value instanceof String[]) {
				String[] val = (String[]) value;
				String s = "";
				if (val.length > 0) {
					for (String element : val)
						if (element != null) {
							s += element + ", ";
						}
				}
				s = s.substring(0, s.length() - 2) + "}";
				value = s;
			} else if (value != null && value.getClass().isArray() && value instanceof Object[]) {
				Object[] val = (Object[]) value;
				String s = "";
				if (val.length > 0) {
					for (Object element : val)
						if (element != null) {
							values.putAll(getValues(element));
						}
				}
				s = s.substring(0, s.length() - 2) + "}";
				value = s;
			} else if (value != null) {
				value = value.toString();
			} else {
				value = "null";
			}
			values.put(name, value.toString());
		}
		return values;
	}

}
