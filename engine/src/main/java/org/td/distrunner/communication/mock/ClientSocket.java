package org.td.distrunner.communication.mock;

import java.util.ArrayList;
import java.util.List;
import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.model.Message;

public class ClientSocket implements IClientSocket {

	public List<Message> Messages = new ArrayList<Message>();

	@Override
	public String sendMessagetoMaster(int messageType, String payLoad) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendMessagetoAddress(int messageType, String payLoad, String url) {
		// TODO Auto-generated method stub
		return null;
	}
}
