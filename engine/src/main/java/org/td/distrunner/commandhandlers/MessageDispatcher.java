package org.td.distrunner.commandhandlers;

import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

import com.google.gson.Gson;

public class MessageDispatcher {

	private static Gson gson = new Gson();
	
	public static void HandleMessage(String incomingMessageText) {
		Message message = gson.fromJson(incomingMessageText, Message.class);
		switch (message.MessageType) {
		case MessageTypes.HeartBeatMessage:
			MasterClientList.giveHeartBeat((String) message.MessageObject);
		default:
			break;
		}
	}
}
