package com.rspsi.core.misc;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {
	
	private static ObjectMapper mapper;
	
	public static ObjectMapper getDefaultMapper() {
		if(mapper == null) {
			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
					.withFieldVisibility(Visibility.ANY).withGetterVisibility(Visibility.NONE));
		}
		return mapper;
	}

}
