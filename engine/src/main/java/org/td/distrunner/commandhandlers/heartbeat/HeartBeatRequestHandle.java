package org.td.distrunner.commandhandlers.heartbeat;

import org.joda.time.DateTime;
import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class HeartBeatRequestHandle implements IRequestHandler<String, String> {

	@Override
	public Message<String> handle(Message<String> message) {
		String clientUniqueId = message.MessageContent;
		// make heartBeat updates to tables
		if (InMemoryObjects.clients.containsKey(clientUniqueId)) // existing
		{
			ClientModel client = InMemoryObjects.clients.get(clientUniqueId);
			client.lastHeartBeat = DateTime.now().toString();
		} else // new
		{
			StringBuilder rawAddress = new StringBuilder(clientUniqueId);
			String clientAddress = rawAddress.substring(rawAddress.indexOf("@") + 1).toString();
			ClientModel client = new ClientModel();
			client.Id = clientUniqueId;
			client.Address = clientAddress;
			client.lastHeartBeat = DateTime.now().toString();
			client.JobCount = 0;
			InMemoryObjects.clients.put(clientUniqueId, client);
		}

		// return client jobs from table
		Message<String> mess = new Message<String>();
		mess.MessageType = MessageTypes.HeartBeatResponseMessage;
		mess.MessageContent = Message.toJsonString(ClientJobModel.getClientJobByClientId(clientUniqueId));

		return mess;
	}
}
