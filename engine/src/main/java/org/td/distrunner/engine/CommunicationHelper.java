package org.td.distrunner.engine;

import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.model.Message;

public class CommunicationHelper {
	
	public static IClientSocket client;
	
	public static void setClientMode(IClientSocket clientImp)
	{
		CommunicationHelper.client = clientImp;
	}
	
	public static String prepareRemoteAddress(String rawAddress) {
		StringBuilder incomimgAddress = new StringBuilder(rawAddress);
		incomimgAddress.deleteCharAt(0);
		incomimgAddress.delete(incomimgAddress.indexOf(":"), incomimgAddress.length());
		return incomimgAddress.toString();
	}

	public static String generateClientId(Message message, String fromAddress) {
		StringBuilder newContent = new StringBuilder(message.MessageContent);
		newContent.append('@');
		newContent.append(fromAddress);
		return newContent.toString();
	}

	public static void sendMessagetoMaster(Message message) throws Exception {
		client.sendMessagetoMaster(message);
	}

	public static void sendMessagetoAddress(Message message, String url)
			throws Exception {
		client.sendMessagetoAddress(message, url);
	}
}
