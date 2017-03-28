package org.td.distrunner.customcommunication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.engine.CommunicationHelper;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class ServerSocket extends HttpServlet {

	private static final long serialVersionUID = -2009851788033359721L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print("Running!");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = IOUtils.toString(req.getInputStream());
		@SuppressWarnings("rawtypes")
		Message messageObj = (Message) JsonHelper.fromJson(message, Message.class);
		if (messageObj.MessageType == MessageTypes.HeartBeatRequestMessage) {
			// small hack to add incoming message fromAddress
			messageObj.MessageContent = CommunicationHelper.generateClientId(messageObj, req.getRemoteAddr());
		}

		// process request message and send response
		@SuppressWarnings("rawtypes")
		Message response = MessageDispatcher.HandleMessage(JsonHelper.getJsonString(messageObj));

		if (response != null) {
			resp.getWriter().print(response.toString());
		}
	}

}
