package org.td.distrunner.wsrelated;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class WebSocketServerChannel extends WebSocketAdapter {
	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		System.out.println("Socket Connected: " + sess);
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		System.out.println("Received TEXT message: " + message);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		System.out.println("Socket Closed: [" + statusCode + "] " + reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}
	
	
}
