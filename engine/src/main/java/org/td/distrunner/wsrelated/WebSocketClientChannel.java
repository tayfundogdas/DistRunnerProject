package org.td.distrunner.wsrelated;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.td.distrunner.commandhandlers.mastersync.MasterCandidatesSyncRequestJob;
import org.td.distrunner.engine.App;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;

public class WebSocketClientChannel {

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

	public static void sendMessagetoMaster(Message message) {
		String url = getMasterWSAddress();

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

	public static void main(String[] args) throws Exception {
		// for test
		UUID uuid = UUID.randomUUID();
		App.AppId = uuid.toString();
		AppSettings.MasterAddress ="127.0.0.1";

		MasterCandidatesSyncRequestJob req = new MasterCandidatesSyncRequestJob();
		req.execute(null);
	}
}
