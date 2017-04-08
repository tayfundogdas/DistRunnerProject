package org.td.distrunner.helpers;

import org.td.distrunner.communication.IClientSocket;

public class CommunicationHelper {

	public static final String CommunicationError = "CommErr";
	public static IClientSocket client;

	public static void setClientMode(IClientSocket clientImp) {
		CommunicationHelper.client = clientImp;
	}

	public static String sendMessagetoMaster(int messageType, String payLoad) {
		return client.sendMessagetoMaster(messageType, payLoad);
	}

	public static String sendMessagetoAddress(int messageType, String payLoad, String url) throws Exception {
		return client.sendMessagetoAddress(messageType, payLoad, url);
	}
}
