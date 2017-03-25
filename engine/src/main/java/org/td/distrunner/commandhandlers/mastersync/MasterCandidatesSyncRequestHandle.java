package org.td.distrunner.commandhandlers.mastersync;

import java.util.List;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class MasterCandidatesSyncRequestHandle implements IRequestHandler<Object,List<String>> {

	@Override
	public Message<List<String>> handle(Message<Object> message) {
		Message<List<String>> mess = new Message<List<String>>();
		mess.MessageType = MessageTypes.MasterSyncResponseMessage;
		mess.MessageContent = InMemoryObjects.getSynchronizeData();
		
		return mess;
	}

}
