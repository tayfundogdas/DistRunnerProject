package org.td.distrunner.engine;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

	public static final ObjectMapper mapper = new ObjectMapper();
	
	public static String getJsonString(Object obj) {
		String result = null;	
		try {
			result = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Object fromJson(String json, @SuppressWarnings("rawtypes") Class cls) {
		Object result = null;
		try {
			result = mapper.readValue(json, cls);
		} catch (Exception e) {
			//LogHelper.logError(e);
		}
		return result;
	}
}