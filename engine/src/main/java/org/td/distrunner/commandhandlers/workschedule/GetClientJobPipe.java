package org.td.distrunner.commandhandlers.workschedule;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ClientJobModel;

public class GetClientJobPipe {
	public static String getClientJob(String payload) {
		String res = null;

		String clientId = payload;
		ClientJobModel item = InMemoryObjects.clientJobs.values().stream()
				.filter(x -> x.AssignedClientId.equals(clientId)).findFirst().orElse(null);
		if (item != null) {
			res = item.toString();
		}

		return res;
	}
}
