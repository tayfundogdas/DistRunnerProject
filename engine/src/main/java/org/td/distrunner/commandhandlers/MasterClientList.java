package org.td.distrunner.commandhandlers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.td.distrunner.model.ClientModel;

public class MasterClientList {
	public static List<ClientModel> clients = new ArrayList<ClientModel>();

	public static void join(String clientUniqueId) {
		ClientModel client = new ClientModel();
		client.Id = clientUniqueId;
		client.lastHeartBeat = DateTime.now();
		clients.add(client);
	}

	public static void giveHeartBeat(String clientUniqueId) {
		ClientModel client = clients.stream().filter(x -> x.Id == clientUniqueId).findFirst().orElse(null);
		if (client != null)
			client.lastHeartBeat = DateTime.now();
	}
}
