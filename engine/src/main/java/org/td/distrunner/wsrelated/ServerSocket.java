package org.td.distrunner.wsrelated;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.engine.CommunicationHelper;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

@WebSocket
public class ServerSocket {

	public Session session;

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	@OnWebSocketMessage
	public void onText(String message) {
		@SuppressWarnings("rawtypes")
		Message messageObj = (Message) JsonHelper.fromJson(message, Message.class);
		if (messageObj.MessageType == MessageTypes.HeartBeatRequestMessage) {
			// small hack to add incoming message fromAddress
			messageObj.MessageContent = CommunicationHelper.generateClientId(messageObj,
					CommunicationHelper.prepareRemoteAddress(this.session.getRemoteAddress().toString()));
		}

		// process request message and send response
		@SuppressWarnings("rawtypes")
		Message response = MessageDispatcher.HandleMessage(JsonHelper.getJsonString(messageObj));

		handleResponseMessage(response);
	}

	private void handleResponseMessage(@SuppressWarnings("rawtypes") Message response) {
		if (response != null) {
			try {
				this.session.getRemote().sendStringByFuture(response.toString());
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}
	}
}
