package org.td.distrunner.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.RunningProcess;

public class InMemoryObjects {
	// active live client list key is client id
	public static ConcurrentHashMap<String, ClientModel> clients = new ConcurrentHashMap<String, ClientModel>();
	// currently running process Cache that parsed from xml
	public static ConcurrentHashMap<String, RuleFlowProcess> processCache = new ConcurrentHashMap<String, RuleFlowProcess>();
	// running process list
	public static ConcurrentHashMap<String, RunningProcess> runningProcessList = new ConcurrentHashMap<String, RunningProcess>();
	// heart beat failure count for starting 0
	public static Byte heartBeatFailCount = 0;
	// unique client id
	public static String AppId = null;

	public static List<String> getSynchronizeData() {
		List<String> transwerObj = new ArrayList<String>();

		return transwerObj;
	}

	public static void setSynchronizeData(List<String> data) {

	}
}
