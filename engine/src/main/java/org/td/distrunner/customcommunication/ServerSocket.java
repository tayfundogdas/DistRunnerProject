package org.td.distrunner.customcommunication;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.engine.CommunicationHelper;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.JettyServer;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class ServerSocket extends HttpServlet {

	private static final long serialVersionUID = -2009851788033359721L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print("Running!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		String message = IOUtils.toString(req.getInputStream(), "UTF-8");
		
		Message messageObj = Message.getMessagefromString(message);
		if (messageObj.MessageType == MessageTypes.HeartBeatRequestMessage) {
			// small hack to add incoming message fromAddress
			messageObj.MessageContent = CommunicationHelper.generateClientId(messageObj, req.getRemoteAddr());
		}

		// process request message and send response
		Message response = MessageDispatcher.HandleMessage(JsonHelper.getJsonString(messageObj));

		if (response != null) {
			resp.getWriter().print(response.toString());
		}
	}

	public static void main(String[] args) {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		InMemoryObjects.AppId = uuid.toString();

		// for logging
		LogHelper.setupLog();

		// start socket and api server valid both for master and slave
		JettyServer.startServer();
	}

}
