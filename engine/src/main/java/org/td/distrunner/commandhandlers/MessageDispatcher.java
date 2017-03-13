package org.td.distrunner.commandhandlers;

import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

import com.google.gson.Gson;

public class MessageDispatcher {

	public void HandleMessage(String incomingMessageText) {
		Gson gson = new Gson();
		Message message = gson.fromJson(incomingMessageText, Message.class);
		switch (message.MessageType) {
		case MessageTypes.RegMessage:
			MasterClientList.join((String) message.MessageObject);
			break;
		default:
			break;
		}
	}
}
