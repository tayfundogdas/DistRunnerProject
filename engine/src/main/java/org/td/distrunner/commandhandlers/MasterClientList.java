package org.td.distrunner.commandhandlers;

import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.td.distrunner.model.ClientModel;

public class MasterClientList {

	public static ConcurrentHashMap<String,ClientModel> clients = new ConcurrentHashMap<String,ClientModel>();

	public static void giveHeartBeat(String clientUniqueId) {
		if (clients.containsKey(clientUniqueId)) // existing
		{
			ClientModel client = clients.get(clientUniqueId);
			client.lastHeartBeat = DateTime.now();
			
		} else // new
		{
			ClientModel client = new ClientModel();
			client.Id = clientUniqueId;
			client.lastHeartBeat = DateTime.now();
			clients.put(clientUniqueId, client);
		}
	}
}
