package org.td.distrunner.commandhandlers.workschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.Node;
import org.td.distrunner.commandhandlers.clientexecutor.ExecuteJob;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.RunningProcess;
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

	private static void advanceCompositeContextNode(Node currNode, RunningProcess currProcess, String executionResult) {
		CompositeContextNode nodeInfo = (CompositeContextNode) currNode;

		Node[] allNodes = nodeInfo.getNodes();

		StartNode startNode = null;
		for (Node node : allNodes) {
			if (node instanceof StartNode)
				startNode = (StartNode) node;
		}

		// if no start node in process start one node parallel
		if (startNode == null && allNodes.length > 0) {
			try {
				@SuppressWarnings("rawtypes")
				List paralelInput = JsonHelper.mapper.readValue(executionResult, new TypeReference<List>() {
				});
				ActionNode currJob = (ActionNode) allNodes[0];
				DroolsConsequenceAction action = (DroolsConsequenceAction) currJob.getAction();
				int i = 1;
				for (Object inp : paralelInput) {

					ClientModel leastUsedClient = getLeastUsedNode();

					if (leastUsedClient != null) {
						ClientJobModel clientJob = new ClientJobModel();
						clientJob.Id = currProcess.CorrelationId + CorrelationSeperator + currNode.getId() + i;
						clientJob.JobName = action.getConsequence();
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

		// move running process
		currProcess.CurrentNode = nodeInfo.getTo().getTo().getId();
		currProcess.WaitingResultNode = allNodes[0].getId();

	}

	private static void scheduleActionToClient(Node currNode, RunningProcess currProcess, ClientModel leastUsedClient,
			String executionResult) {
		ActionNode nodeInfo = (ActionNode) currNode;
		DroolsConsequenceAction action = (DroolsConsequenceAction) nodeInfo.getAction();
		ClientJobModel clientJob = new ClientJobModel();
		clientJob.Id = currProcess.CorrelationId + CorrelationSeperator + currNode.getId();
		clientJob.JobName = action.getConsequence();
		clientJob.JobParam = executionResult;
		clientJob.AssignedClientId = leastUsedClient.Id;

		// put message to waiting queue
		try {
			InMemoryObjects.clientJobs.put(clientJob.Id, clientJob);
			// increment client job count
			leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
			LogHelper.logTrace("Job assigned to client with info CorrId : " + currProcess.CorrelationId + " JobName : "
					+ clientJob.JobName + " JobParam : " + clientJob.JobParam.substring(0,
							clientJob.JobParam.length() > 50 ? 50 : clientJob.JobParam.length()));
		} catch (Exception e) {
			LogHelper.logError(e);
		}

		// move running process
		currProcess.CurrentNode = nodeInfo.getTo().getTo().getId();
		currProcess.WaitingResultNode = currNode.getId();
	}

	private static void processNode(Node currNode, RunningProcess currProcess, String executionResult) {

		ClientModel leastUsedClient = getLeastUsedNode();
		if (leastUsedClient == null) {
			// if no available node found
			LogHelper.logTrace("No node to schedule for");
			LogHelper.logTrace(currProcess);
		} else {
			// if available client to run found

			// get flow node and process it
			// for complex jobs
			if (currNode instanceof CompositeContextNode) {
				advanceCompositeContextNode(currNode, currProcess, executionResult);
			}

			// for single job assign to client
			if (currNode instanceof ActionNode) {
				scheduleActionToClient(currNode, currProcess, leastUsedClient, executionResult);
			}

		}

	}

	private static void clearClientJobsforFinishedProcess(String correlationId) {
		for (String jobId : InMemoryObjects.clientJobs.keySet()) {
			if (jobId.startsWith(correlationId))
				InMemoryObjects.clientJobs.remove(jobId);
		}
	}

	// after handleJobResult
	private static void advanceNodeWithJobResult(String jobId, String executionResult) {
		if (jobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator) <= 0) {
			LogHelper.logTrace("Bad jobid for : " + jobId);
			return;
		}

		// remove client job for result
		if (InMemoryObjects.clientJobs.containsKey(jobId))
			InMemoryObjects.clientJobs.remove(jobId);

		String processCorrelationId = jobId.substring(0, jobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator));
		// get process and check if its exist
		RunningProcess currProcess = InMemoryObjects.runningProcessList.get(processCorrelationId);

		if (currProcess == null) {
			// clear client job list for finished process
			// clearClientJobsforFinishedProcess(processCorrelationId);
			// LogHelper.logTrace("No process for : " + correlationId);
			return;
		}

		// look if process finished and remove from process table
		String cacheName = currProcess.Id
				.substring(currProcess.Id.indexOf(MasterWorkSchedulingJob.CorrelationSeperator) + 1);
		RuleFlowProcess mainFlow = InMemoryObjects.processCache.get(cacheName);

		// set after start node if process just started
		if (currProcess.CurrentNode == -1)
			currProcess.CurrentNode = mainFlow.getStart(null).getTo().getTo().getId();

		// set after start node if process just started
		if (currProcess.WaitingResultNode == -1)
			currProcess.WaitingResultNode = mainFlow.getStart(null).getTo().getTo().getId();
		else {
			String currJobResutId = jobId.substring(jobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator) + 1);
			if (Long.parseLong(currJobResutId) != currProcess.WaitingResultNode) {
				// Redundant result we skip it
				return;
			}
		}

		// process main process
		processNode(mainFlow.getNode(currProcess.CurrentNode), currProcess, executionResult);

		// test if flow ended
		if (mainFlow.getNode(currProcess.CurrentNode).getOutgoingConnections().isEmpty()) {
			// remove finished process
			InMemoryObjects.runningProcessList.remove(currProcess.CorrelationId);
			LogHelper.logTrace("Process finished : " + currProcess.CorrelationId);
			return;
		}
	}

	// start process first item and return correlationId for process public
	public static String startProcess(String processName, String firstParam) throws Exception {
		// create process unique id
		String correlationId = UUID.randomUUID().toString();
		RunningProcess process = new RunningProcess();
		process.CorrelationId = correlationId;
		process.Id = correlationId + CorrelationSeperator + processName;
		// put process to global table
		InMemoryObjects.runningProcessList.put(process.CorrelationId, process);
		// make logging
		LogHelper.logTrace("Process started : " + process.CorrelationId);
		// make ready process first item to run
		advanceNodeWithJobResult(process.Id, firstParam);
		// return process correlationId to track return
		return process.CorrelationId;
	}

	// if a result come from assigned node schedule new job public static
	public static void handleJobResult(String jobId, String executionResult) {
		if (executionResult.equals(ExecuteJob.JobFailMesage)) {
			// LogHelper.logTrace("job failed on client");
			// TODO:What do to in case of error in job?
		} else
			advanceNodeWithJobResult(jobId, executionResult);
	}

	// if no heart beat from scheduled node reschedule to new node public
	static void rescheduleJob(ClientJobModel job) {

	}

}
