package org.td.distrunner.communication.custom;

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

}
