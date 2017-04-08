package org.td.distrunner.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.NodeModel;

public class InMemoryObjects {
	// active live client list key is client id on master
	public static ConcurrentHashMap<String, ClientModel> clients = new ConcurrentHashMap<String, ClientModel>();
	// currently running process cache that parsed from xml on master
	public static ConcurrentHashMap<String, RuleFlowProcess> processCache = new ConcurrentHashMap<String, RuleFlowProcess>();
	// clients jobs list on master
	public static ConcurrentHashMap<String, ClientJobModel> clientJobs = new ConcurrentHashMap<String, ClientJobModel>();
	// running process list on master
	public static ConcurrentHashMap<String, NodeModel> runningProcessList = new ConcurrentHashMap<String, NodeModel>();
	// heart beat failure count for starting 0 on client
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
