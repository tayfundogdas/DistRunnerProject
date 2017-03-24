package org.td.distrunner.commandhandlers.workschedule;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.td.distrunner.commandhandlers.heartbeat.HeartBeatRequestHandle;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.model.ProcessModel;
import org.td.distrunner.processmodelparser.ProcessfromXML;

//this method try to schedule jobs as parallel as possible by examining data dependencies
public class MasterWorkSchedulingJob {

	private static ClientModel getLeastUsedNode() {
		// find least used node
		List<ClientModel> list = new ArrayList<ClientModel>(InMemoryObjects.clients.values());
		Collections.sort(list, (a, b) -> a.JobCount > b.JobCount ? 1 : a.JobCount == b.JobCount ? 0 : -1);

		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	private static void assignSinglejob(ProcessModel currProcess) {
		ClientModel leastUsedClient = getLeastUsedNode();

		if (leastUsedClient == null) {
			// if no available node found
			LogHelper.logTrace("No node to schedule for:" + currProcess);
		} else {
			// if available node found

			// prepare job and put job table
			ClientJobModel clientJob = new ClientJobModel();
			clientJob.Id = currProcess.CorrelationId + "_" + currProcess.Id;
			clientJob.AssignedClientId = leastUsedClient.Id;
			clientJob.JobName = currProcess.Executable;
			clientJob.WaitingClientId = "";
			// put job to the job table
			InMemoryObjects.clientsJobs.put(clientJob.Id, clientJob);
			// increment client job count
			leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
		}
	}

	private static void markWaitingNodes(HashMap<String, String> relations, String correlationId) {
		// try get waiting client id from lookup table
		for (String from : relations.keySet()) {
			ClientJobModel fromClient = InMemoryObjects.clientsJobs.get(correlationId + "_" + from);

			if (fromClient != null) {
				ClientJobModel toClient = InMemoryObjects.clientsJobs.get(correlationId + "_" + relations.get(from));
				if (toClient != null) {
					fromClient.WaitingClientId = toClient.AssignedClientId;
				}
			}
		}
	}

	//TODO:Test with multi sub process with inner processes examples
	private static void replaceSubProcessRelation(HashMap<String, String> relations, ProcessModel subProcess) {
		//if sub process only the process with no start stop point
		if (subProcess.RelationTable.size() == 0 && subProcess.StartNodeId == null
				&& subProcess.SubProcesses.size() > 0) {
			for (String from : relations.keySet()) {
				if (relations.get(from).equals(subProcess.Id))
					relations.put(from, subProcess.SubProcesses.get(0).Id);
			}
		}
	}

	private static void assignCompoundjob(List<ProcessModel> processList, HashMap<String, String> relations,
			String correlationId) {
		for (ProcessModel innerProcess : processList) {
			innerProcess.CorrelationId = correlationId;
			if (innerProcess.Executable == null) {
				// compound node
				replaceSubProcessRelation(relations, innerProcess);
				assignCompoundjob(innerProcess.SubProcesses, innerProcess.RelationTable, correlationId);
			} else {
				// action node
				assignSinglejob(innerProcess);
			}
		}

		markWaitingNodes(relations, correlationId);
	}

	// schedule process' all items to least used working nodes by pre-allocating
	// nodes
	public static void scheduleJob(ProcessModel process) {
		// put process in process list
		String correlationId = UUID.randomUUID().toString();
		process.CorrelationId = correlationId;
		// assign all nodes of process
		assignCompoundjob(process.SubProcesses, process.RelationTable, correlationId);
		LogHelper.logTrace(InMemoryObjects.clientsJobs);
	}

	// if a result come from assigned node schedule send result to waiting node
	// and report result time for suggesting better optimized blocks
	public static void handleJobResult() {

	}

	// if no heart beat from scheduled node reschedule to new node
	public static void rescheduleJob(ClientJobModel job) {
		ClientModel leastUsedClient = getLeastUsedNode();
		// reassign job
		job.AssignedClientId = leastUsedClient.Id;
		// increment client job count
		leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
	}

	public static void main(String[] args) throws Exception {
		// for logging
		LogHelper.setupLog();

		// for setup application id
		InMemoryObjects.AppId = UUID.randomUUID().toString();

		// create mock clients
		HeartBeatRequestHandle clientRequest = new HeartBeatRequestHandle();
		Message heartbeatMessage = new Message();
		heartbeatMessage.MessageType = MessageTypes.HeartBeatRequestMessage;
		for (int i = 1; i <= 5; ++i) {
			heartbeatMessage.MessageObject = InMemoryObjects.AppId + "@127.0.0." + i;
			clientRequest.handle(heartbeatMessage);
		}

		String content;
		content = new String(Files.readAllBytes(Paths.get("D:\\StringProcessor.bpmn")));
		ProcessModel result = ProcessfromXML.getFromXml(content);
		scheduleJob(result);
	}
}
