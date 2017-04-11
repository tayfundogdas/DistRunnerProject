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
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.NodeModel;
import org.td.distrunner.processmodelparser.ProcessParser;

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

	private static void advanceSubProcess(NodeModel currNode, String param, String processCorrelationId) {

		// start one node parallel
		if (currNode.SubItems.size() == 1) {
			try {
				@SuppressWarnings("rawtypes")
				List paralelInput = JsonHelper.mapper.readValue(param, new TypeReference<List>() {
				});
				int i = 1;
				for (Object inp : paralelInput) {

					ClientModel leastUsedClient = getLeastUsedNode();

					if (leastUsedClient != null) {
						ClientJobModel clientJob = new ClientJobModel();
						clientJob.Id = processCorrelationId + CorrelationSeperator + currNode.SubItems.get(0).Id + "_"
								+ i;
						clientJob.JobName = currNode.SubItems.get(0).ExecutableName;
						clientJob.JobParam = JsonHelper.getJsonString(inp);
						clientJob.AssignedClientId = leastUsedClient.Id;

						// put message to waiting queue
						InMemoryObjects.clientJobs.put(clientJob.Id, clientJob);
						// increment client job count
						leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
						LogHelper.logTrace("Job assigned to client with info CorrId : " + processCorrelationId
								+ " JobName : " + clientJob.JobName + " JobParam : " + clientJob.JobParam.substring(0,
										clientJob.JobParam.length() > 50 ? 50 : clientJob.JobParam.length()));
					}
					i = i + 1;
				}
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}

		// move running process
		NodeModel currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);
		currProcess.CurrentNode = currProcess.CurrentNode + 1;
	}

	private static void scheduleActionToClient(NodeModel currNode, ClientModel leastUsedClient, String param,
			String processCorrelationId) {
		NodeModel currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);

		ClientJobModel clientJob = new ClientJobModel();
		clientJob.Id = processCorrelationId + CorrelationSeperator + currNode.Id + CorrelationSeperator
				+ currProcess.CurrentNode;
		clientJob.JobName = currNode.ExecutableName;
		clientJob.JobParam = param;
		clientJob.AssignedClientId = leastUsedClient.Id;

		// put message to waiting queue
		InMemoryObjects.clientJobs.put(clientJob.Id, clientJob);
		// increment client job count
		leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
		LogHelper.logTrace("Job assigned to client with info CorrId : " + processCorrelationId + " JobName : "
				+ clientJob.JobName + " JobParam : "
				+ clientJob.JobParam.substring(0, clientJob.JobParam.length() > 50 ? 50 : clientJob.JobParam.length()));
	}

	private static void processNode(NodeModel currNode, String param, String processCorrelationId) {

		ClientModel leastUsedClient = getLeastUsedNode();
		if (leastUsedClient == null) {
			// if no available node found
			LogHelper.logTrace("No node to schedule for");
			LogHelper.logTrace(currNode);
		} else {
			// if available client to run found

			// get flow node and process it
			// for complex jobs
			if (currNode.getIsSubProcess()) {
				advanceSubProcess(currNode, param, processCorrelationId);
			}

			// for single job assign to client
			if (currNode.getIsExecutable()) {
				scheduleActionToClient(currNode, leastUsedClient, param, processCorrelationId);
			}

			// move running process
			NodeModel currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);
			currProcess.CurrentNode = currProcess.CurrentNode + 1;

		}

	}

	// after handleJobResult
	private static void advanceNodeWithParam(String processCorrelationId, String param) {
		// get process and check if its exist
		NodeModel currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);

		// look if process finished and remove from process table
		if (currProcess.CurrentNode >= currProcess.SubItems.size()) {
			// remove finished process
			InMemoryObjects.runningProcessList.remove(currProcess.CorrelationId);
			LogHelper.logTrace("Process finished : " + currProcess.CorrelationId);
			return;
		}

		// process
		processNode(currProcess.SubItems.get(currProcess.CurrentNode), param, processCorrelationId);
	}

	// start process first item and return correlationId for process public
	public static String startProcess(String processName, String firstParam) throws Exception {
		// create process unique id
		String correlationId = UUID.randomUUID().toString();
		NodeModel process = ProcessParser.getProcessTree(processName);
		process.CorrelationId = correlationId;
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
	public static String handleJobResult(String payLoad) {
		ExecutionResultModel executionResult = ExecutionResultModel.getFromString(payLoad);
		if (executionResult.ExecutionResult.equals(ExecuteJob.JobFailMesage)) {
			// LogHelper.logTrace("job failed on client");
			// TODO:What do to in case of error in job?
		} else {
			// remove client job for result
			if (InMemoryObjects.clientJobs.containsKey(executionResult.JobId))
				InMemoryObjects.clientJobs.remove(executionResult.JobId);
			
			String processCorrelationId = executionResult.JobId.substring(0,
					executionResult.JobId.indexOf(CorrelationSeperator));
			NodeModel currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);

			if (currProcess == null) {
				// clear client job list for finished process
				// clearClientJobsforFinishedProcess(processCorrelationId);
				// LogHelper.logTrace("No process for : " + correlationId);
				return null;
			}
			
			Integer stepId = -1;
			try
			{
				stepId = Integer.parseInt(executionResult.JobId.substring(
						executionResult.JobId.lastIndexOf(CorrelationSeperator) + 1, executionResult.JobId.length()));
			}
			catch (Exception e) {
				// TODO: handle exception
			}

			if (stepId == (currProcess.CurrentNode-1)) {
				advanceNodeWithParam(processCorrelationId, executionResult.ExecutionResult);
			}
		}

		return null;
	}

	// if no heart beat from scheduled node reschedule to new node public
	static void rescheduleJob(ClientJobModel job) {

	}

}
