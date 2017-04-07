package org.td.distrunner.communication.mock;

import java.util.ArrayList;
import java.util.List;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
public class ClientSocket implements IClientSocket {

	public List<Message> Messages = new ArrayList<Message>();


	public void sendMessagetoAddress(Message message, String url) throws Exception {

	}

	@Override
	public String sendMessagetoMaster(int messageType, String payLoad) {
		// TODO Auto-generated method stub
		return null;
	}
}
