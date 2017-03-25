package org.td.distrunner.commandhandlers.mastersync;

import java.util.List;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.Message;

public class MasterCandidatesSyncResponseHandle implements IRequestHandler<List<String>, Object> {

	@Override
	public Message<Object> handle(Message<List<String>> message) {
		List<String> data = message.MessageContent;
		InMemoryObjects.setSynchronizeData(data);

		return null;
	}

}
