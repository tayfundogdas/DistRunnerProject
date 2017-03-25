package org.td.distrunner.commandhandlers.workschedule;

import java.lang.reflect.Type;
import java.util.StringTokenizer;
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
		Type listType = new TypeToken<ExecutionResultModel>() {}.getType();
		Gson gson = new Gson();
		ExecutionResultModel reportedResult  = gson.fromJson(message.MessageContent, listType);
		
		StringTokenizer tokens = new StringTokenizer(reportedResult.JobId.replace(MasterWorkSchedulingJob.CorrelationSeperator, ' '));
		while(tokens.hasMoreTokens())
		{
			// handle process execution
			String processCorrelationId = tokens.nextToken();
			if (!processCorrelationId.equals(""))
				MasterWorkSchedulingJob.handleJobResult(processCorrelationId);
		}
		
		// remove reported job from client job table
		InMemoryObjects.clientsJobs.remove(reportedResult.JobId);

		return null;
	}

}
