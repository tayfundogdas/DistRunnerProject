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
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.RunningProcess;

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

	private static void advanceCompositeContextNode(Node currNode, RunningProcess currProcess,
			ClientModel leastUsedClient, String executionResult) {
		CompositeContextNode nodeInfo = (CompositeContextNode) currNode;

		Node[] allNodes = nodeInfo.getNodes();

		StartNode startNode = null;
		for (Node node : allNodes) {
			if (node instanceof StartNode)
				startNode = (StartNode) node;
		}

		// if no start node in process start one node
		if (startNode == null && allNodes.length > 0)
			scheduleActionToClient(allNodes[0], currProcess, leastUsedClient, executionResult);

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
			LogHelper.logTrace("Job assigned to client");
			LogHelper.logTrace(clientJob.JobName);
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}

	private static void processNode(Node currNode, RunningProcess currProcess, String executionResult) {

		ClientModel leastUsedClient = getLeastUsedNode();
		if (leastUsedClient == null) {
			// if no available node found
			LogHelper.logTrace("No node to schedule for");
			LogHelper.logTrace(currProcess);
			LogHelper.logTrace(leastUsedClient == null);
		} else {
			// if available client to run found

			// get flow node and process it
			// for complex jobs
			if (currNode instanceof CompositeContextNode) {
				advanceCompositeContextNode(currNode, currProcess, leastUsedClient, executionResult);
				// move running process
				CompositeContextNode nodeInfo = (CompositeContextNode) currNode;
				currProcess.CurrentNode = nodeInfo.getTo().getTo().getId();
			}

			// for single job assign to client
			if (currNode instanceof ActionNode) {
				scheduleActionToClient(currNode, currProcess, leastUsedClient, executionResult);
				// move running process
				ActionNode nodeInfo = (ActionNode) currNode;
				currProcess.CurrentNode = nodeInfo.getTo().getTo().getId();
			}

		}

	}

	// after handleJobResult
	private static void advanceProcess(String correlationId, String executionResult) {
		// get process and check if its exist
		RunningProcess currProcess = InMemoryObjects.runningProcessList.get(correlationId);
		if (currProcess == null) {
			//clear client job list for finished process
			for (String jobId : InMemoryObjects.clientJobs.keySet()) {
				if (jobId.startsWith(correlationId))
					InMemoryObjects.clientJobs.remove(jobId);
			}
			LogHelper.logTrace("No process for:" + correlationId);
			return;
		}

		// look if process finished and remove from process table
		RuleFlowProcess mainFlow = InMemoryObjects.processCache.get(currProcess.Id);

		// set after start node if process just started
		if (currProcess.CurrentNode == -1)
			currProcess.CurrentNode = mainFlow.getStart(null).getTo().getTo().getId();

		if (mainFlow.getNode(currProcess.CurrentNode).getOutgoingConnections().isEmpty()) {
			// remove finished process
			InMemoryObjects.runningProcessList.remove(currProcess.CorrelationId);
			// clear client jobs about this process
			InMemoryObjects.clientJobs.remove(correlationId);
			LogHelper.logTrace("Process finished");
			LogHelper.logTrace(currProcess);
			return;
		}

		// process main process
		processNode(mainFlow.getNode(currProcess.CurrentNode), currProcess, executionResult);

	}

	// start process first item and return correlationId for process public
	public static String startProcess(String processName, String firstParam) throws Exception {
		// create process unique id
		String correlationId = UUID.randomUUID().toString();
		RunningProcess process = new RunningProcess();
		process.Id = processName;
		process.CorrelationId = correlationId;
		// put process to global table
		InMemoryObjects.runningProcessList.put(process.CorrelationId, process);
		// make logging
		LogHelper.logTrace("Process started");
		LogHelper.logTrace(process);
		// make ready process first item to run
		advanceProcess(correlationId, firstParam);
		// return process correlationId to track return
		return process.CorrelationId;
	}

	// if a result come from assigned node schedule new job public static
	public static void handleJobResult(String correlationId, String executionResult) {
		advanceProcess(correlationId, executionResult);
	}

	// if no heart beat from scheduled node reschedule to new node public
	static void rescheduleJob(ClientJobModel job) {

	}

}
