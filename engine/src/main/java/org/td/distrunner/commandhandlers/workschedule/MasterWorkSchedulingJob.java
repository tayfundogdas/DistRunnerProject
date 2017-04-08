package org.td.distrunner.commandhandlers.workschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.td.distrunner.commandhandlers.clientexecutor.ExecuteJob;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.NodeModel;
import com.fasterxml.jackson.core.type.TypeReference;

//this method try to schedule jobs as parallel as possible by examining data dependencies
public class MasterWorkSchedulingJob {

	public static final char CorrelationSeperator = '*';

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

	private static void advanceSubProcess(NodeModel currProcess, String param) {

		// start one node parallel
		if (currProcess.SubItems.size() == 0)
		{
			try {
				@SuppressWarnings("rawtypes")
				List paralelInput = JsonHelper.mapper.readValue(param, new TypeReference<List>() {
				});
				int i = 1;
				for (Object inp : paralelInput) {

					ClientModel leastUsedClient = getLeastUsedNode();

					if (leastUsedClient != null) {
						ClientJobModel clientJob = new ClientJobModel();
						clientJob.Id = currProcess.CorrelationId + CorrelationSeperator + currProcess.Id + i;
						clientJob.JobName = currProcess.ExecutableName;
						clientJob.JobParam = JsonHelper.getJsonString(inp);
						clientJob.AssignedClientId = leastUsedClient.Id;

						// put message to waiting queue
						InMemoryObjects.clientJobs.put(clientJob.Id, clientJob);
						// increment client job count
						leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
						LogHelper.logTrace("Job assigned to client with info CorrId : " + currProcess.CorrelationId
								+ " JobName : " + clientJob.JobName + " JobParam : " + clientJob.JobParam.substring(0,
										clientJob.JobParam.length() > 50 ? 50 : clientJob.JobParam.length()));
					}
					i = i + 1;
				}
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}
	}

	private static void scheduleActionToClient(NodeModel currProcess, ClientModel leastUsedClient, String param) {
		ClientJobModel clientJob = new ClientJobModel();
		clientJob.Id = currProcess.CorrelationId + CorrelationSeperator + currProcess.Id;
		clientJob.JobName = currProcess.ExecutableName;
		clientJob.JobParam = param;
		clientJob.AssignedClientId = leastUsedClient.Id;

		// put message to waiting queue
		InMemoryObjects.clientJobs.put(clientJob.Id, clientJob);
		// increment client job count
		leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
		LogHelper.logTrace("Job assigned to client with info CorrId : " + currProcess.CorrelationId + " JobName : "
				+ clientJob.JobName + " JobParam : "
				+ clientJob.JobParam.substring(0, clientJob.JobParam.length() > 50 ? 50 : clientJob.JobParam.length()));
	}

	private static void processNode(NodeModel currProcess, String param) {

		ClientModel leastUsedClient = getLeastUsedNode();
		if (leastUsedClient == null) {
			// if no available node found
			LogHelper.logTrace("No node to schedule for");
			LogHelper.logTrace(currProcess);
		} else {
			// if available client to run found

			// get flow node and process it
			// for complex jobs
			if (currProcess.getIsSubProcess()) {
				advanceSubProcess(currProcess, param);
			}

			// for single job assign to client
			if (currProcess.getIsExecutable()) {
				scheduleActionToClient(currProcess, leastUsedClient, param);
			}

			// move running process
			currProcess.CurrentNode = currProcess.CurrentNode + 1;

		}

	}

	// after handleJobResult
	private static void advanceNodeWithParam(String processCorrelationId, String param) {
		// remove client job for result
		if (InMemoryObjects.clientJobs.containsKey(processCorrelationId))
			InMemoryObjects.clientJobs.remove(processCorrelationId);

		// get process and check if its exist
		NodeModel currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);

		if (currProcess == null) {
			// clear client job list for finished process
			// clearClientJobsforFinishedProcess(processCorrelationId);
			// LogHelper.logTrace("No process for : " + correlationId);
			return;
		}

		// look if process finished and remove from process table
		if (currProcess.CurrentNode == NodeModel.ProcessEnd) {
			// remove finished process
			InMemoryObjects.runningProcessList.remove(currProcess.CorrelationId);
			LogHelper.logTrace("Process finished : " + currProcess.CorrelationId);
			return;
		}

		// process
		processNode(currProcess.SubItems.get(currProcess.CurrentNode), param);
	}

	// start process first item and return correlationId for process public
	public static String startProcess(String processName, String firstParam) throws Exception {
		// create process unique id
		String correlationId = UUID.randomUUID().toString();
		NodeModel process = new NodeModel();
		process.CorrelationId = correlationId;
		process.Id = processName;
		// put process to global table
		InMemoryObjects.runningProcessList.put(process.CorrelationId, process);
		// make logging
		LogHelper.logTrace("Process started : " + process.CorrelationId);
		// make ready process first item to run
		advanceNodeWithParam(process.CorrelationId, firstParam);
		// return process correlationId to track return
		return process.CorrelationId;
	}

	// if a result come from assigned node schedule new job public static
	public static void handleJobResult(String processCorrelationId, String executionResult) {
		if (executionResult.equals(ExecuteJob.JobFailMesage)) {
			// LogHelper.logTrace("job failed on client");
			// TODO:What do to in case of error in job?
		} else
			advanceNodeWithParam(processCorrelationId, executionResult);
	}

	// if no heart beat from scheduled node reschedule to new node public
	static void rescheduleJob(ClientJobModel job) {

	}

}
