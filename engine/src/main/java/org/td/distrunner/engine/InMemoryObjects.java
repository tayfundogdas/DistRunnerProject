package org.td.distrunner.engine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.ProcessModel;
import org.td.distrunner.model.ClientJobModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InMemoryObjects {
	// active live client list key is client id
	public static ConcurrentHashMap<String, ClientModel> clients = new ConcurrentHashMap<String, ClientModel>();
	// key is job id
	public static ConcurrentHashMap<String, ClientJobModel> clientsJobs = new ConcurrentHashMap<String, ClientJobModel>();
	// active submitted process list
	public static ConcurrentHashMap<String, ProcessModel> processes = new ConcurrentHashMap<String, ProcessModel>();
	// heart beat failure count for starting 0
	public static Byte heartBeatFailCount = 0;
	// unique client id
	public static String AppId = null;

	public static List<String> getSynchronizeData() {
		List<String> transwerObj = new ArrayList<String>();
		Gson gson = new Gson();
		transwerObj.add(gson.toJson(clients.values()));
		transwerObj.add(gson.toJson(clientsJobs.values()));

		return transwerObj;
	}

	public static void setSynchronizeData(List<String> data) {
		Gson gson = new Gson();
		Type listType = new TypeToken<Collection<ClientModel>>() {
		}.getType();
		Collection<ClientModel> clientList = gson.fromJson(data.get(0), listType);
		clientList.forEach(x -> {
			if (!clients.containsKey(x.Id))
				clients.put(x.Id, x);
		});

		listType = new TypeToken<Collection<ClientJobModel>>() {
		}.getType();
		Collection<ClientJobModel> jobList = gson.fromJson(data.get(1), listType);
		jobList.forEach(x -> {
			if (!clientsJobs.containsKey(x.Id))
				clientsJobs.put(x.Id, x);
		});
	}
}
