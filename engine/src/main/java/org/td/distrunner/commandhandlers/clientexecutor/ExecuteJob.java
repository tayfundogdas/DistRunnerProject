package org.td.distrunner.commandhandlers.clientexecutor;

import java.util.List;

import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ExecutionResultModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

public class ExecuteJob {
	
	public static void executeJobsAndReportResulttoMaster(List<ClientJobModel> myJobs) {
		// run all job list and report result
		for (ClientJobModel job : myJobs) {
			Message<String> mess = new Message<String>();
			mess.MessageType = MessageTypes.ExecutionResultMessage;
			ExecutionResultModel result = new ExecutionResultModel();
			result.JobId = job.Id;
			result.ExecutionResult = null;
			mess.MessageContent = Message.toJsonString(result);
			try {
				WebSocketClientChannel.sendMessagetoMaster(mess);
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}

	}
}
