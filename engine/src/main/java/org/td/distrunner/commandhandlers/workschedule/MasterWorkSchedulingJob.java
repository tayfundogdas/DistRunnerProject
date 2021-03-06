package org.td.distrunner.commandhandlers.workschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.ProcessModel;
import org.td.distrunner.processmodelparser.JarHelper;

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

	// after handleJobResult
	private static void advanceProcess(String correlationId, Object executionResult) {
		// get process and check if its exist
		ProcessModel currProcess = InMemoryObjects.processes.get(correlationId);
		if (currProcess == null) {
			LogHelper.logTrace("No process for:" + correlationId);
			return;
		}

		// look if process finished and remove from process table
		if (currProcess.NextProcessId != null && currProcess.NextProcessId.equals(currProcess.EndNodeId)) {
			// remove finished process
			InMemoryObjects.processes.remove(correlationId);
			LogHelper.logTrace("Process finished");
			LogHelper.logTrace(currProcess);
			// advance upper process
			if (currProcess.Id.indexOf(CorrelationSeperator) > 0) {
				StringTokenizer upperCorrelationsIds = new StringTokenizer(
						currProcess.Id.replace(CorrelationSeperator, ' '));
				// find all upper processes
				List<String> upperProcesses = new ArrayList<String>();
				while (upperCorrelationsIds.hasMoreTokens()) {
					upperProcesses.add(upperCorrelationsIds.nextToken());
				}
				// finish all upper processes in order
				for (int i = upperProcesses.size() - 2; i >= 0; --i) {
					handleJobResult(upperProcesses.get(i), null);
				}
			}
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
				// for sub process initialize it
				correlationId = initProcess(nextJob, currProcess.CorrelationId, executionResult);
			} else {
				// for single job assign to client
				ClientJobModel clientJob = new ClientJobModel();
				clientJob.Id = currProcess.CorrelationId + CorrelationSeperator + currProcess.NextProcessId;
				clientJob.AssignedClientId = leastUsedClient.Id;
				clientJob.JobName = currProcess.SubProcesses.get(currProcess.NextProcessId).Executable;
				clientJob.JobParam = executionResult;

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

	private static String initProcess(ProcessModel process, String upperCorrelationId, Object firstParam) {
		// create process unique id
		String correlationId = UUID.randomUUID().toString();
		process.CorrelationId = correlationId;
		// set id
		if (!upperCorrelationId.equals(""))
			process.Id = upperCorrelationId + CorrelationSeperator + process.Id;
		// put future process as after start
		process.NextProcessId = process.RelationTable.get(process.StartNodeId);
		// put process to global table
		InMemoryObjects.processes.put(process.CorrelationId, process);
		// make logging
		LogHelper.logTrace("Process started");
		LogHelper.logTrace(process);
		// make ready process first item to run
		advanceProcess(correlationId, firstParam);
		// return process correlationId to track
		return process.CorrelationId;
	}

	// start process first item and return correlationId for process
	public static String startProcess(String processName, Object firstParam) throws Exception {
		ProcessModel process = JarHelper.getProcessByName(processName);
		return initProcess(process, "", firstParam);
	}

	// if a result come from assigned node schedule new job
	public static void handleJobResult(String correlationId, Object executionResult) {
		advanceProcess(correlationId, executionResult);
	}

	// if no heart beat from scheduled node reschedule to new node
	public static void rescheduleJob(ClientJobModel job) {
		ClientModel leastUsedClient = getLeastUsedNode();
		// reassign job
		job.AssignedClientId = leastUsedClient.Id;
		// increment client job count
		leastUsedClient.JobCount = leastUsedClient.JobCount + 1;
	}

}
