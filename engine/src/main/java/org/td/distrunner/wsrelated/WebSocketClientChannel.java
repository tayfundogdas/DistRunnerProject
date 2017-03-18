package org.td.distrunner.wsrelated;

import java.net.URI;
import java.util.concurrent.Future;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;

public class WebSocketClientChannel extends WebSocketAdapter {

	public static String getMasterWSAddress() {
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

	public static void sendMessagetoMaster(Message message) throws Exception {
		String url = getMasterWSAddress();

		URI uri = URI.create(url);

		WebSocketClient client = new WebSocketClient();

		client.start();
		// The socket that receives events
		WebSocketClientChannel socket = new WebSocketClientChannel();
		// Attempt Connect
		Future<Session> fut = client.connect(socket, uri);
		// Wait for Connect
		Session session = fut.get();
		// Send a message
		session.getRemote().sendString(message.getJsonForm());
		// Close session
		session.close();
		client.stop();
	}
}
