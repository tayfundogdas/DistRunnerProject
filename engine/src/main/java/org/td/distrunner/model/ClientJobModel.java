package org.td.distrunner.model;

import java.util.ArrayList;
import java.util.List;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.JsonHelper;

public class ClientJobModel {
	public String Id;
	public String JobName;
	public Object JobParam;
	public String AssignedClientId;

	public static List<ClientJobModel> getClientJobsByClientId(String clientId) {
		List<ClientJobModel> result = new ArrayList<ClientJobModel>();
		for (ClientJobModel job : InMemoryObjects.clientJobs.values())
			if (job.AssignedClientId.equals(clientId))
				result.add(job);
		return result;
	}

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
}
