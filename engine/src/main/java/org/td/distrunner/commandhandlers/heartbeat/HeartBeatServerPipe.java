package org.td.distrunner.commandhandlers.heartbeat;

import org.joda.time.DateTime;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ClientModel;

public class HeartBeatServerPipe {
	public static String handleHeartBeat(String payload, String clientAddress) {
		String res = null;

		String clientUniqueId = payload;
		// make heartBeat updates to tables
		if (InMemoryObjects.clients.containsKey(clientUniqueId)) // existing
		{
			ClientModel client = InMemoryObjects.clients.get(clientUniqueId);
			client.lastHeartBeat = DateTime.now().toString();
		} else // new
		{
			ClientModel client = new ClientModel();
			client.Id = clientUniqueId + '@' + clientAddress;
			client.Address = clientAddress;
			client.lastHeartBeat = DateTime.now().toString();
			client.JobCount = 0;
			// init client
			InMemoryObjects.clients.put(clientUniqueId, client);
		}

		return res;
	}
}
