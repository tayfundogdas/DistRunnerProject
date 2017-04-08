package org.td.distrunner.model;

import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionResultModel {
	public String JobId;
	public String ExecutionResult;
	
	public static ExecutionResultModel getFromString(String json) {
		ExecutionResultModel result = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(json, new TypeReference<ExecutionResultModel>() {
			});
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return JsonHelper.getJsonString(this);
	}
}
