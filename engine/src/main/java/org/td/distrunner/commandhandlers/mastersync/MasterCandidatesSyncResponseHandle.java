package org.td.distrunner.commandhandlers.mastersync;

import java.util.List;
import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.JsonHelper;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.Message;

import com.fasterxml.jackson.core.type.TypeReference;

public class MasterCandidatesSyncResponseHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		List<String> data = null;
		try {
			data = JsonHelper.mapper.readValue(message.MessageContent, new TypeReference<List<String>>() {
			});
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		if (data != null)
			InMemoryObjects.setSynchronizeData(data);

		return null;
	}

}
