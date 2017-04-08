package org.td.distrunner.communication.custom;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.td.distrunner.commandhandlers.heartbeat.HeartBeatServerPipe;
import org.td.distrunner.commandhandlers.workschedule.GetClientJobPipe;
import org.td.distrunner.commandhandlers.workschedule.MasterWorkSchedulingJob;
import org.td.distrunner.model.MessageTypes;

public class ServerSocket extends HttpServlet {

	private static final long serialVersionUID = -2009851788033359721L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print("Running!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int messageType = Integer.parseInt(req.getPathInfo().substring(1));
		String payload = IOUtils.toString(req.getInputStream(), "UTF-8");

		String response = null;
		switch (messageType) {
		case MessageTypes.HeartBeatMessage:
			response = HeartBeatServerPipe.handleHeartBeat(payload, req.getRemoteAddr());
			break;
		case MessageTypes.GetJobMessage:
			response = GetClientJobPipe.getClientJob(payload);
			break;
		case MessageTypes.ExecutionResultMessage:
			response = MasterWorkSchedulingJob.handleJobResult(payload);
			break;
		default:
			break;
		}

		resp.setCharacterEncoding("UTF-8");
		if (response != null) {
			resp.getWriter().print(response.toString());
		}
	}

}
