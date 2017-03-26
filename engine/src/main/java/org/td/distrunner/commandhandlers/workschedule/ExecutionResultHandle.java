package org.td.distrunner.commandhandlers.workschedule;

import java.lang.reflect.Type;
import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.EnableLogging;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ExecutionResultHandle implements IRequestHandler<String, Object> {

	@Override
	@EnableLogging
	public Message<Object> handle(Message<String> message) {
		Type listType = new TypeToken<ExecutionResultModel>() {
		}.getType();
		Gson gson = new Gson();
		ExecutionResultModel reportedResult = gson.fromJson(message.MessageContent, listType);

		//process advance
		String processCorrelationId = reportedResult.JobId.substring(0,
				reportedResult.JobId.indexOf(MasterWorkSchedulingJob.CorrelationSeperator));
		MasterWorkSchedulingJob.handleJobResult(processCorrelationId);

		// remove reported job from client job table
		InMemoryObjects.clientsJobs.remove(reportedResult.JobId);

		return null;
	}

}
