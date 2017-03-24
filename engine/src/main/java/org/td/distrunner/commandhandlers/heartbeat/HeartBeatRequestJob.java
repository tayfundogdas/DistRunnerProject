package org.td.distrunner.commandhandlers.heartbeat;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.td.distrunner.commandhandlers.assignmaster.AssignNewMasterJob;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.JobRegisterHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

public class HeartBeatRequestJob implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//if master is up send heart beat message
		if (InMemoryObjects.heartBeatFailCount < AppSettings.HeartBeatTreshold) {
			Message mess = new Message();
			mess.MessageType = MessageTypes.HeartBeatRequestMessage;
			mess.MessageObject = InMemoryObjects.AppId;
			try
			{
				WebSocketClientChannel.sendMessagetoMaster(mess);
			}
			catch (Exception e) {
				InMemoryObjects.heartBeatFailCount = (byte) (InMemoryObjects.heartBeatFailCount + 1);
				LogHelper.logError(e);
			}
		}
		else //if master is down stop all jobs
		{
			JobRegisterHelper.cancelAllJobsIfMasterDied();
			
			//if this node is master candidate signal NewMasterMessage
			if (AppSettings.IsMasterCandidate == 1) {
				AssignNewMasterJob.broadcastNewMasterMessagetoNodes();
			}
		}			
	}		
}
