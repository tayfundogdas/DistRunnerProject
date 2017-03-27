package org.td.distrunner.commandhandlers.workschedule;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.EnableLogging;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;

public class ExecutionResultHandle implements IRequestHandler<String, Object> {

	@Override
	@EnableLogging
	public Message<Object> handle(Message<String> message) {

		ExecutionResultModel reportedResult = (ExecutionResultModel) JsonHelper.fromJson(message.MessageContent,ExecutionResultModel.class);

		// process advance
		String processCorrelationId = reportedResult.JobId.substring(0,
				reportedResult.JobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator));
		MasterWorkSchedulingJob.handleJobResult(processCorrelationId, reportedResult.ExecutionResult);

		return null;
	}

}
