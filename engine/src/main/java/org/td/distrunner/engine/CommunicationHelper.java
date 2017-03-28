package org.td.distrunner.engine;

import org.td.distrunner.model.Message;
import org.td.distrunner.customcommunication.ClientSocket;

public class CommunicationHelper {
	public static String prepareRemoteAddress(String rawAddress) {
		StringBuilder incomimgAddress = new StringBuilder(rawAddress);
		incomimgAddress.deleteCharAt(0);
		incomimgAddress.delete(incomimgAddress.indexOf(":"), incomimgAddress.length());
		return incomimgAddress.toString();
	}

	public static String generateClientId(Message<String> message, String fromAddress) {
		StringBuilder newContent = new StringBuilder(message.MessageContent);
		newContent.append('@');
		newContent.append(fromAddress);
		return newContent.toString();
	}

	public static void sendMessagetoMaster(@SuppressWarnings("rawtypes") Message message) throws Exception {
		ClientSocket client = new ClientSocket();
		client.sendMessagetoMaster(message);
	}

	public static void sendMessagetoAddress(@SuppressWarnings("rawtypes") Message message, String url)
			throws Exception {
		ClientSocket client = new ClientSocket();
		client.sendMessagetoAddress(message, url);
	}
}
