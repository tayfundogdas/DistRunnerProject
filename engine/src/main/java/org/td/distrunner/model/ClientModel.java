package org.td.distrunner.model;

import com.google.gson.Gson;

public class ClientModel {
	public String Id;
	public String lastHeartBeat;
	public String Address;
	public Integer JobCount;
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
