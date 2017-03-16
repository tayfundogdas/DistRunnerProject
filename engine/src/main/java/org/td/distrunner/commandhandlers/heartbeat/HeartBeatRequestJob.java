package org.td.distrunner.commandhandlers.heartbeat;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.td.distrunner.engine.App;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

public class HeartBeatRequestJob implements Job
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Message mess = new Message();
		mess.MessageType = MessageTypes.HeartBeatRequestMessage;
		mess.MessageObject = App.AppId;
		WebSocketClientChannel.sendMessagetoMaster(mess);
	}
}
