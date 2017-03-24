package org.td.distrunner.model;

import com.google.gson.Gson;

public class ClientJobModel {
	public String Id;
	public String AssignedClientId;
	public String JobName;
	public String WaitingClientId;
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
