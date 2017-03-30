package org.td.distrunner.commandhandlers.workschedule;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;
import com.fasterxml.jackson.core.type.TypeReference;

public class ExecutionResultHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		ExecutionResultModel reportedResult = null;
		try {
			reportedResult = JsonHelper.mapper.readValue(message.MessageContent,
					new TypeReference<ExecutionResultModel>() {
					});
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		// process advance
		if (reportedResult != null && reportedResult.JobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator) > 1) {
			String processCorrelationId = reportedResult.JobId.substring(0,
					reportedResult.JobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator));
			MasterWorkSchedulingJob.handleJobResult(processCorrelationId, reportedResult.ExecutionResult);
		}
		return null;
	}

}
