package org.td.distrunner.commandhandlers.heartbeat;

import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.model.HeartBeatModel;
import org.td.distrunner.model.MessageTypes;

public class HeartBeatClientPipe {
	public static void sendHeartBeat() {
		HeartBeatModel mess = new HeartBeatModel();
		CommunicationHelper.sendMessagetoMaster(MessageTypes.HeartBeatMessage, mess.toString());
	}
}
