package org.td.distrunner.helpers;

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
}
