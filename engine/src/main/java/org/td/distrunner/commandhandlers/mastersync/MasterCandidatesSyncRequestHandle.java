package org.td.distrunner.commandhandlers.mastersync;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class MasterCandidatesSyncRequestHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		Message mess = new Message();
		mess.MessageType = MessageTypes.MasterSyncResponseMessage;
		mess.MessageObject = InMemoryObjects.getSynchronizeData();
		
		return mess;
	}

}
