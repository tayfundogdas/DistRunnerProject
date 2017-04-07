package org.td.distrunner.commandhandlers.heartbeat;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.model.HeartBeatModel;
import org.td.distrunner.model.MessageTypes;

public class HeartBeatClientPipe {
	public static void sendHeartBeat() {
		HeartBeatModel mess = new HeartBeatModel();
		String res = CommunicationHelper.sendMessagetoMaster(MessageTypes.HeartBeatMessage, mess.toString());
		if (res == CommunicationHelper.CommunicationError)
			InMemoryObjects.heartBeatFailCount = (byte) (InMemoryObjects.heartBeatFailCount + 1);
	}
}
