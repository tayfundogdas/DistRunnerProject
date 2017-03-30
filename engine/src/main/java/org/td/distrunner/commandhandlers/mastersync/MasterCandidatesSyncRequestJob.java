package org.td.distrunner.commandhandlers.mastersync;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.td.distrunner.engine.CommunicationHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

//sync client and job tables from current master in interval to master candidates stated in settings
public class MasterCandidatesSyncRequestJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Message mess = new Message();
		mess.MessageType = MessageTypes.MasterSyncRequestMessage;
		try {
			CommunicationHelper.sendMessagetoMaster(mess);
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}

}
