package org.td.distrunner.communication;

import org.td.distrunner.model.Message;

public interface IClientSocket {
	void sendMessagetoMaster(Message message) throws Exception;
	void sendMessagetoAddress(Message message, String url) throws Exception;
}
