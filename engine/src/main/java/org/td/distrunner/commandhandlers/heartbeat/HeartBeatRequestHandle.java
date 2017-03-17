package org.td.distrunner.commandhandlers.heartbeat;

import org.joda.time.DateTime;
import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.Message;

public class HeartBeatRequestHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		String clientUniqueId = (String) message.MessageObject;
		if (InMemoryObjects.clients.containsKey(clientUniqueId)) // existing
		{
			ClientModel client = InMemoryObjects.clients.get(clientUniqueId);
			client.lastHeartBeat = DateTime.now().toString();
		} else // new
		{
			StringBuilder rawAddress = new StringBuilder((String) message.MessageObject);
			String clientAddress = rawAddress.substring(rawAddress.indexOf("@")).toString();
			ClientModel client = new ClientModel();
			client.Id = clientUniqueId;
			client.Address = clientAddress;
			client.lastHeartBeat = DateTime.now().toString();
			InMemoryObjects.clients.put(clientUniqueId, client);
		}

		return null;
	}
}
