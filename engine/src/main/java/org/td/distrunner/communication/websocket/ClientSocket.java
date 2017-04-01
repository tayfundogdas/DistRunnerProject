package org.td.distrunner.communication.websocket;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;

@WebSocket
public class ClientSocket implements IClientSocket {

	private Session session;
	CountDownLatch latch = new CountDownLatch(1);

	@OnWebSocketConnect
	public void onConnect(Session session) {
		//LogHelper.logTrace("Connected to server");
		this.session = session;
		latch.countDown();
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		//LogHelper.logTrace("Socket disconnected Status : " + statusCode + " Reason : " + reason);
	}

	@OnWebSocketError
	public void onError(Session session, Throwable error) {
		LogHelper.logError(error);
	}

	@OnWebSocketMessage
	public void onText(Session session, String message) throws Exception {
		// process response message
		MessageDispatcher.HandleMessage(message);
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	private String getMasterWSAddress() {
		StringBuilder str = new StringBuilder();
		str.append(AppSettings.WSChannelName);
		str.append("://");
		str.append(AppSettings.MasterAddress);
		str.append(":");
		str.append(AppSettings.JettyPort);
		str.append("/");
		str.append(AppSettings.WSChannelName);
		str.append("/");

		return str.toString();
	}

	private void sendMessage(String str) {
		try {
			session.getRemote().sendString(str);
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}

	public void sendMessagetoMaster(Message message) throws Exception {
		String url = getMasterWSAddress();

		ClientSocket socket = new ClientSocket();
		WebSocketClient client = new WebSocketClient();
		client.start();
		URI uri = URI.create(url);
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		client.connect(socket, uri, request);
		socket.getLatch().await();
		socket.sendMessage(message.toString());

		client.stop();
	}

	public void sendMessagetoAddress(Message message, String url)
			throws Exception {

		ClientSocket socket = new ClientSocket();
		WebSocketClient client = new WebSocketClient();
		client.start();
		URI uri = URI.create(url);
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		client.connect(socket, uri, request);
		socket.getLatch().await();
		socket.sendMessage(message.toString());

		client.stop();
	}

}
