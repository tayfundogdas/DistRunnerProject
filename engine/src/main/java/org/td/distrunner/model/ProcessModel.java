package org.td.distrunner.model;

import java.util.HashMap;

import com.google.gson.Gson;

public class ProcessModel {
	// internal id
	public String Id;
	// for compound nodes
	public String StartNodeId;
	public String EndNodeId;
	public HashMap<String, String> RelationTable;
	public HashMap<String, ProcessModel> SubProcesses;
	// for single nodes
	public String Executable;
	// for engine tracking
	public String CorrelationId;
	public String NextProcessId;

	public ProcessModel() {
		this.SubProcesses = new HashMap<String, ProcessModel>();
	}

	public ProcessModel(String id, String executable) {
		this.Id = id;
		this.Executable = executable;
		this.SubProcesses = new HashMap<String, ProcessModel>();
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
