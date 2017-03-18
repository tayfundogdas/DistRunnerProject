package org.td.distrunner.engine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.JobCountModel;
import org.td.distrunner.model.JobModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InMemoryObjects {
	// active live client list key is client id
	public static ConcurrentHashMap<String, ClientModel> clients = new ConcurrentHashMap<String, ClientModel>();
	// key is job id
	public static ConcurrentHashMap<String, JobModel> jobs = new ConcurrentHashMap<String, JobModel>();
	// key is client id and value is job count on client
	public static ConcurrentHashMap<String, JobCountModel> clientJobsCount = new ConcurrentHashMap<String, JobCountModel>();
	//heart beat failure count for starting 0
	public static Byte heartBeatFailCount = 0;

	public static List<String> getSynchronizeData() {
		List<String> transwerObj = new ArrayList<String>();
		Gson gson = new Gson();
		transwerObj.add(gson.toJson(clients.values()));
		transwerObj.add(gson.toJson(jobs.values()));
		transwerObj.add(gson.toJson(clientJobsCount.values()));
		
		return transwerObj;
	}

	public static void setSynchronizeData(List<String> data) {	
		Gson gson = new Gson();
		Type listType = new TypeToken<Collection<ClientModel>>(){}.getType();
		Collection<ClientModel> clientList = gson.fromJson(data.get(0), listType);
		clientList.forEach(x ->  {
			if (!clients.containsKey(x.Id)) 
				clients.put(x.Id, x);
		});
		
		listType = new TypeToken<Collection<JobModel>>(){}.getType();
		Collection<JobModel> jobList = gson.fromJson(data.get(1), listType);
		jobList.forEach(x ->  {
			if (!jobs.containsKey(x.Id))
				jobs.put(x.Id, x);
		});
		
		listType = new TypeToken<Collection<JobCountModel>>(){}.getType();
		Collection<JobCountModel> jobcountList = gson.fromJson(data.get(2), listType);
		jobcountList.forEach(x ->  {
			if (!clientJobsCount.containsKey(x.Id))
				clientJobsCount.put(x.Id, x);
		});
	}
}
