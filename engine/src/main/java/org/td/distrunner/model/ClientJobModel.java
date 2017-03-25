package org.td.distrunner.model;

import java.util.List;
import java.util.stream.Collectors;
import org.td.distrunner.engine.InMemoryObjects;
import com.google.gson.Gson;

public class ClientJobModel {
	public String Id;
	public String AssignedClientId;
	public String JobName;
	
	public static List<ClientJobModel> getClientJobByClientId(String clientId)
	{	
		List<ClientJobModel> result = InMemoryObjects.clientsJobs.values().stream()
				.filter(x->x.AssignedClientId.equals(clientId))
				.collect(Collectors.toList());
		
		return result;
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
