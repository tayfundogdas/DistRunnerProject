package org.td.distrunner.apirelated;

import java.io.PrintWriter;

import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ClientModel;

public class ClientListOperation {

	public static void printClientList(PrintWriter output)
	{
		output.print("<h1>Clients</h1>");
		StringBuilder res = new StringBuilder();
		for (ClientModel client : InMemoryObjects.clients.values()) {
			res.append(client.Id);
			res.append("<->");
			res.append(client.lastHeartBeat);
			res.append("<br>");
		}
		output.print(res);
	}
}
