package org.td.distrunner.communication.mock;

import java.util.ArrayList;
import java.util.List;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.engine.CommunicationHelper;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
public class ClientSocket implements IClientSocket {

	public List<Message> Messages = new ArrayList<Message>();

	private Message handleMessage(Message messageObj) {
		if (messageObj.MessageType == MessageTypes.HeartBeatRequestMessage) {
			// small hack to add incoming message fromAddress
			messageObj.MessageContent = CommunicationHelper.generateClientId(messageObj, "127.0.0.1");
		}

		// process request message and send response
		Message response = MessageDispatcher.HandleMessage(JsonHelper.getJsonString(messageObj));

		return response;
	}

	public void sendMessagetoMaster(Message message) throws Exception {
		this.Messages.add(message);
		Message response = handleMessage(message);
		if (response != null) {
			this.Messages.add(response);
			handleMessage(response);
		}
	}

	public void sendMessagetoAddress(Message message, String url) throws Exception {

	}
}
