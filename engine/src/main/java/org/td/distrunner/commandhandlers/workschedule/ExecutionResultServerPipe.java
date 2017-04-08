package org.td.distrunner.commandhandlers.workschedule;

import org.td.distrunner.model.ExecutionResultModel;

public class ExecutionResultServerPipe {
	public static String handleExecutionResult(String payload) {
		String res = null;

		ExecutionResultModel reportedResult = ExecutionResultModel.getFromString(payload);
		// process advance
		if (reportedResult != null) {
			MasterWorkSchedulingJob.handleJobResult(reportedResult.JobId, reportedResult.ExecutionResult);
		}
		return res;
	}
}
