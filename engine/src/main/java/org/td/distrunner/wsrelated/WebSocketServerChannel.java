package org.td.distrunner.wsrelated;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.Message;

public class WebSocketServerChannel extends WebSocketAdapter {

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		// process request message and send response
		@SuppressWarnings("rawtypes")
		Message response = MessageDispatcher.HandleMessage(message,
				prepareRemoteAddress(super.getSession().getRemoteAddress().toString()));
		handleResponseMessage(response);
	}

	private String prepareRemoteAddress(String rawAddress) {
		StringBuilder incomimgAddress = new StringBuilder(rawAddress);
		incomimgAddress.deleteCharAt(0);
		incomimgAddress.delete(incomimgAddress.indexOf(":"), incomimgAddress.length());
		return incomimgAddress.toString();
	}

	private void handleResponseMessage(@SuppressWarnings("rawtypes") Message response) {
		if (response != null) {
			try {
				super.getSession().getRemote().sendString(response.toString());
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}
	}
	
}
