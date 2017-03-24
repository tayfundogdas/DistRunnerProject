package org.td.distrunner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

public class ProcessModel {
	// internal id
	public String Id;
	// for compound nodes
	public String StartNodeId;
	public String EndNodeId;
	public HashMap<String, String> RelationTable;
	public List<ProcessModel> SubProcesses;
	// for single nodes
	public String Executable;
	// for engine tracking
	public String CorrelationId;

	public ProcessModel() {
		this.SubProcesses = new ArrayList<ProcessModel>();
	}

	public ProcessModel(String id, Boolean isParallel) {
		this.Id = id;
		this.SubProcesses = new ArrayList<ProcessModel>();
	}

	public ProcessModel(String id, String executable) {
		this.Id = id;
		this.Executable = executable;
		this.SubProcesses = new ArrayList<ProcessModel>();
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
