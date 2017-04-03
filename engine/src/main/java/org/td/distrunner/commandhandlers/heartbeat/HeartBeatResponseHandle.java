package org.td.distrunner.commandhandlers.heartbeat;

import java.util.List;
import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.Message;

import com.fasterxml.jackson.core.type.TypeReference;

public class HeartBeatResponseHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		List<ClientJobModel> myJobs = null;
		try {
			myJobs = JsonHelper.mapper.readValue(message.MessageContent, new TypeReference<List<ClientJobModel>>() {
			});
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		if (myJobs != null) {
			// sync my job list from server message
			// add new items
			for (ClientJobModel job : myJobs) {
				if (!InMemoryObjects.currentNodeJobList.containsKey(job.Id))
					InMemoryObjects.currentNodeJobList.put(job.Id, job);
			}
		}

		return null;
	}
}
