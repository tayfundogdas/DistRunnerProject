package org.td.distrunner.commandhandlers.heartbeat;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.model.MessageTypes;

public class HeartBeatClientPipe {
	public static void sendHeartBeat() {
		String res = CommunicationHelper.sendMessagetoMaster(MessageTypes.HeartBeatMessage,InMemoryObjects.AppId);
		if (res == CommunicationHelper.CommunicationError)
			InMemoryObjects.heartBeatFailCount = (byte) (InMemoryObjects.heartBeatFailCount + 1);
	}
}
