package org.td.distrunner.apirelated;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ClientModel;

public class ClientListOperation {

	public static void printClientList(HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		response.getWriter().print("<h1>Clients</h1>");
		StringBuilder res = new StringBuilder();
		for (ClientModel client : InMemoryObjects.clients.values()) {
			res.append(client.Id);
			res.append("<->");
			res.append(client.lastHeartBeat);
			res.append("<br>");
		}
		response.getWriter().print(res);
	}
}
