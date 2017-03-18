package org.td.distrunner.wsrelated;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.model.Message;

public class WebSocketServerChannel extends WebSocketAdapter {

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		// process request message and send response
		Message response = MessageDispatcher.HandleMessage(message,
				prepareRemoteAddress(super.getSession().getRemoteAddress().toString()));
		handleResponseMessage(response);
		System.out.println("Received TEXT message: " + message);
	}

	private String prepareRemoteAddress(String rawAddress) {
		StringBuilder incomimgAddress = new StringBuilder(rawAddress);
		incomimgAddress.deleteCharAt(0);
		incomimgAddress.delete(incomimgAddress.indexOf(":"), incomimgAddress.length());
		return incomimgAddress.toString();
	}

	private void handleResponseMessage(Message response) {
		if (response != null) {
			try {
				super.getSession().getRemote().sendString(response.getJsonForm());
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}
	
}
