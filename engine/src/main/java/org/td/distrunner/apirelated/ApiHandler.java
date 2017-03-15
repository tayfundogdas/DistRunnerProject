package org.td.distrunner.apirelated;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.td.distrunner.commandhandlers.MasterClientList;
import org.td.distrunner.model.ClientModel;

public class ApiHandler extends HttpServlet {

	private static final long serialVersionUID = -2009821448033352521L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);

		String path = request.getPathInfo();

		switch (path) {
		case "/ClientList":
			response.getWriter().println("<h1>Clients</h1>");
			StringBuilder res = new StringBuilder();
			for (ClientModel client : MasterClientList.clients.values()) {
				res.append(client.Id);
				res.append("<->");
				res.append(client.lastHeartBeat);
				res.append("<br>");
			}
			response.getWriter().println(res);
			break;
		default:
			response.getWriter().println("<h1>Hello World</h1>");
			break;
		}
	}
}
