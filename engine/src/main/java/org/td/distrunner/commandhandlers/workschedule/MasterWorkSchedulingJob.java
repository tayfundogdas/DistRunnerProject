package org.td.distrunner.commandhandlers.workschedule;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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

	private static void advanceProcess(String correlationId) {
		// get process and check if its exist
		ProcessModel currProcess = InMemoryObjects.processes.get(correlationId);
		if (currProcess == null) {
			LogHelper.logTrace("No process for:" + correlationId);
			return;
		}

		if (currProcess.NextProcessId != null && currProcess.NextProcessId.equals(currProcess.EndNodeId)) {
			LogHelper.logTrace("Process finished");
			LogHelper.logTrace(currProcess);
			return;
		}

		ClientModel leastUsedClient = getLeastUsedNode();
		if (leastUsedClient == null) {
			// if no available node found
			LogHelper.logTrace("No node to schedule for");
			LogHelper.logTrace(currProcess);
		} else {
			// if available node found
			// prepare job and put job table
			ProcessModel nextJob = currProcess.SubProcesses.get(currProcess.NextProcessId);
			if (nextJob.Executable == null) {
				// for sub process start it
				correlationId = startProcess(nextJob);
			} else {
				// for single job assign to client
				ClientJobModel clientJob = new ClientJobModel();
				clientJob.Id = currProcess.CorrelationId + "_" + currProcess.NextProcessId;
				clientJob.AssignedClientId = leastUsedClient.Id;
				clientJob.JobName = currProcess.SubProcesses.get(currProcess.NextProcessId).Executable;

				// put job to the job table
				InMemoryObjects.clientsJobs.put(clientJob.Id, clientJob);
				// increment client job count
				leastUsedClient.JobCount = leastUsedClient.JobCount + 1;

				LogHelper.logTrace("Job assigned to client");
				LogHelper.logTrace(clientJob);
			}

			// put future process as after start
			currProcess.NextProcessId = currProcess.RelationTable.get(currProcess.NextProcessId);
		}

	}

	// start process first item and return correlationId for process
	public static String startProcess(ProcessModel process) {
		// create process unique id
		process.CorrelationId = UUID.randomUUID().toString();
		// put future process as after start
		process.NextProcessId = process.RelationTable.get(process.StartNodeId);
		InMemoryObjects.processes.put(process.CorrelationId, process);
		LogHelper.logTrace("Process started");
		LogHelper.logTrace(process);
		return process.CorrelationId;
	}

	// if a result come from assigned node schedule send result to waiting node
	// and report result time for suggesting better optimized blocks
	public static void handleJobResult(String correlationId) {

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
		ProcessModel process = ProcessfromXML.getFromXml(content);
		String correlationId = startProcess(process);
		handleJobResult(correlationId);
	}
}
