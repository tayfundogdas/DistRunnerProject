package org.td.distrunner.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.JobModel;

import com.google.gson.internal.LinkedTreeMap;

public class InMemoryObjects {
	// active live client list
	public static ConcurrentHashMap<String, ClientModel> clients = new ConcurrentHashMap<String, ClientModel>();
	// key is job id
	public static ConcurrentHashMap<String, JobModel> jobs = new ConcurrentHashMap<String, JobModel>();
	// key is clientuniqueid value is job count on client
	public static ConcurrentHashMap<String, Integer> clientJobsCount = new ConcurrentHashMap<String, Integer>();
	
	public static LinkedTreeMap<String, List<Object>> getSynchronizeData()
	{
		LinkedTreeMap<String, List<Object>> tables = new LinkedTreeMap<String, List<Object>>();
		
		List<Object> clientList = new ArrayList<Object>();
		for (ClientModel clientInfo : clients.values()) {
			clientList.add(clientInfo);
		}
		
		tables.put("Clients", clientList);
		
		return tables;
	}
	
	public static void setSynchronizeData(LinkedTreeMap<String, List<Object>> data)
	{
		
	}
}
