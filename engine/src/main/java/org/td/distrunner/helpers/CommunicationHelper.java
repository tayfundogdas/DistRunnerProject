package org.td.distrunner.helpers;

import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.model.Message;

public class CommunicationHelper {

	public static final String CommunicationError = "CommErr";
	public static IClientSocket client;

	public static void setClientMode(IClientSocket clientImp) {
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

	public static String sendMessagetoMaster(int messageType, String payLoad) {
		return client.sendMessagetoMaster(messageType, payLoad);
	}

	public static String sendMessagetoAddress(int messageType, String payLoad, String url) throws Exception {
		return client.sendMessagetoAddress(messageType, payLoad, url);
	}
}
