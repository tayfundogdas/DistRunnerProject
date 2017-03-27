package org.td.distrunner.wsrelated;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

@WebSocket
public class ServerSocket {

	public ClientList clientlist;
	public Session session;

	public ServerSocket(ClientList clientlist) {
		this.clientlist = clientlist;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
	}

	private String prepareRemoteAddress(String rawAddress) {
		StringBuilder incomimgAddress = new StringBuilder(rawAddress);
		incomimgAddress.deleteCharAt(0);
		incomimgAddress.delete(incomimgAddress.indexOf(":"), incomimgAddress.length());
		return incomimgAddress.toString();
	}

	private String generateClientId(Message<String> message, String fromAddress) {
		StringBuilder newContent = new StringBuilder(message.MessageContent);
		newContent.append('@');
		newContent.append(fromAddress);
		return newContent.toString();
	}

	@SuppressWarnings("unchecked")
	@OnWebSocketMessage
	public void onText(String message) {
		@SuppressWarnings("rawtypes")
		Message messageObj = (Message) JsonHelper.fromJson(message, Message.class);
		if (messageObj.MessageType == MessageTypes.HeartBeatRequestMessage) {
			// small hack to add incoming message fromAddress
			messageObj.MessageContent = generateClientId(messageObj,
					prepareRemoteAddress(this.session.getRemoteAddress().toString()));

			ClientList.getInstance().join((String) messageObj.MessageContent, this);
			
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
