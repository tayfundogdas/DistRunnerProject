package org.td.distrunner.commandhandlers.workschedule;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.commandhandlers.clientexecutor.ExecuteJob;
import org.td.distrunner.engine.EnableLogging;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.Message;

public class ExecutionRequestHandle implements IRequestHandler<String, Object> {

	@Override
	@EnableLogging
	public Message<Object> handle(Message<String> message) {

		ClientJobModel job = (ClientJobModel) JsonHelper.fromJson(message.MessageContent, ClientJobModel.class);
		ExecuteJob.executeJobAndReportResulttoMaster(job);

		return null;
	}

}
