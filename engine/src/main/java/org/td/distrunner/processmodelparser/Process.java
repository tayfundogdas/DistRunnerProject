package org.td.distrunner.processmodelparser;

import java.util.ArrayList;
import java.util.List;

public class Process {

	public String Id;
	public String StartNodeId;
	public String EndNodeId;
	public String Executable;
	public Boolean IsParallel;

	public List<Process> SubProcesses;

	public Process(String id, Boolean isParallel) {
		this.Id = id;
		this.SubProcesses = new ArrayList<Process>();
		this.IsParallel = isParallel;
	}

	public Process(String id, String executable) {
		this.Id = id;
		this.Executable = executable;
	}

	public Boolean isCodeActionNode() {
		return this.SubProcesses == null && this.Executable.length() > 0;
	}

}
