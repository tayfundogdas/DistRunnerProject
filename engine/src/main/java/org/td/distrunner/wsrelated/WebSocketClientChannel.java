package org.td.distrunner.wsrelated;

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class WebSocketClientChannel {

	public String getMasterWSAddress() {
		StringBuilder str = new StringBuilder();
		str.append("ws://");
		str.append(AppSettings.MasterAddress);
		str.append(":");
		str.append(AppSettings.JettyPort);
		str.append("/");
		str.append(AppSettings.WSChannelName);
		str.append("/");

		return str.toString();
	}

	public void sendMessage(String url, Message message) {
		URI uri = URI.create(url);

		WebSocketClient client = new WebSocketClient();
		try {
			try {
				client.start();
				// The socket that receives events
				WebSocketServerChannel socket = new WebSocketServerChannel();
				// Attempt Connect
				Future<Session> fut = client.connect(socket, uri);
				// Wait for Connect
				Session session = fut.get();
				// Send a message
				session.getRemote().sendString(message.getJsonForm());
				// Close session
				session.close();
			} finally {
				client.stop();
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");

		WebSocketClientChannel wsClient = new WebSocketClientChannel();
		Message dummyMessage= new Message();
		dummyMessage.MessageType = MessageTypes.DummyMessage;
		dummyMessage.MessageObject = "Hello";
		wsClient.sendMessage(wsClient.getMasterWSAddress(), dummyMessage);

	}
}
