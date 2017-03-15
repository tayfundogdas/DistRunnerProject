package org.td.distrunner.commandhandlers;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.td.distrunner.engine.App;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

public class SlaveHeartBeatJob implements Job
{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		WebSocketClientChannel wsClient = new WebSocketClientChannel();
		Message hbMessage = new Message();
		hbMessage.MessageType = MessageTypes.HeartBeatMessage;
		hbMessage.MessageObject = App.AppId;
		wsClient.sendMessage(wsClient.getMasterWSAddress(), hbMessage);
	}
}
