package org.td.distrunner.model;

import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientJobModel {
	public String Id;
	public String JobName;
	public String JobParam;
	public String AssignedClientId;

	@Override
	public boolean equals(Object obj) {
		boolean sameSame = false;
		if (obj != null && obj instanceof ClientJobModel) {
			sameSame = this.Id == ((ClientJobModel) obj).Id;
		}
		return sameSame;
	}

	@Override
	public String toString() {
		return JsonHelper.getJsonString(this);
	}
	
	public static ClientJobModel getFromString(String json) {
		ClientJobModel result = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(json, new TypeReference<ClientJobModel>() {
			});
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		return result;
	}
}