package org.td.distrunner.model;

import org.td.processmodel.CodeAction;

import com.google.gson.Gson;

public class ClientJobModel {
	public String Id;
	public String AssignedClientId;
	public CodeAction jobContent;
	public String WaitingClientId;
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
