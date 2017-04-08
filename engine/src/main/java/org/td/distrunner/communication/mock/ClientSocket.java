package org.td.distrunner.communication.mock;

import java.util.ArrayList;
import java.util.List;

import org.td.distrunner.commandhandlers.heartbeat.HeartBeatServerPipe;
import org.td.distrunner.commandhandlers.workschedule.GetClientJobPipe;
import org.td.distrunner.commandhandlers.workschedule.MasterWorkSchedulingJob;
import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class ClientSocket implements IClientSocket {

	public List<Message> Messages = new ArrayList<Message>();

	@Override
	public String sendMessagetoMaster(int messageType, String payLoad) {
		String response = null;
		switch (messageType) {
		case MessageTypes.HeartBeatMessage:
			response = HeartBeatServerPipe.handleHeartBeat(payLoad, "127.0.0.1");
			break;
		case MessageTypes.GetJobMessage:
			response = GetClientJobPipe.getClientJob(payLoad);
			break;
		case MessageTypes.ExecutionResultMessage:
			response = MasterWorkSchedulingJob.handleJobResult(payLoad);
			break;
		default:
			break;
		}

		return response;
	}

	@Override
	public String sendMessagetoAddress(int messageType, String payLoad, String url) {
		// TODO Auto-generated method stub
		return null;
	}
}
