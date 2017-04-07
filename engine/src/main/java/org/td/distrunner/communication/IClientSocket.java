package org.td.distrunner.communication;

public interface IClientSocket {
	String sendMessagetoMaster(int messageType, String payLoad);
	String sendMessagetoAddress(int messageType, String payLoad, String url);
}
