package org.td.distrunner.communication;

import org.td.distrunner.model.Message;

public interface IClientSocket {
	String sendMessagetoMaster(int messageType, String payLoad);
	void sendMessagetoAddress(Message message, String url) throws Exception;
}
