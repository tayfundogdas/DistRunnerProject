package org.td.distrunner.model;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HeartBeatModel {
	public int Message = MessageTypes.HeartBeatMessage;
	public String AppId = InMemoryObjects.AppId;

	public static HeartBeatModel getFromString(String json) {
		HeartBeatModel result = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(json, new TypeReference<HeartBeatModel>() {
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
