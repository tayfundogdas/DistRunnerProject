package org.td.distrunner.commandhandlers.mastersync;

import java.util.List;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.Message;

public class MasterCandidatesSyncResponseHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		@SuppressWarnings("unchecked")
		List<String> data = (List<String>) message.MessageObject;
		InMemoryObjects.setSynchronizeData(data);

		return null;
	}

}
