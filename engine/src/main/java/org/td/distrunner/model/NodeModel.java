package org.td.distrunner.model;

import java.util.List;

public class NodeModel {

	public static final Integer ProcessStart = 0;
	public static final Integer ProcessEnd = -1;

	public String Id;
	public String CorrelationId;
	public Integer CurrentNode = ProcessStart;
	public String AssignedClientId;
	public String ExecutableName;
	public List<NodeModel> SubItems;

	public Boolean getIsSubProcess() {
		return this.SubItems != null;
	}

	public Boolean getIsExecutable() {
		return this.ExecutableName != null && this.ExecutableName.length() > 0;
	}

}
