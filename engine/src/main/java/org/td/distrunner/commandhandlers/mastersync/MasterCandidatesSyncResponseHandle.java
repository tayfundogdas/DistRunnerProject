package org.td.distrunner.commandhandlers.mastersync;

import java.util.List;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.Message;

import com.google.gson.internal.LinkedTreeMap;

public class MasterCandidatesSyncResponseHandle implements IRequestHandler {

	@Override
	public Message handle(Message message) {
		 @SuppressWarnings("unchecked")
		 LinkedTreeMap<String, List<Object>> data= (LinkedTreeMap<String, List<Object>>)message.MessageObject; 	
		 InMemoryObjects.setSynchronizeData(data);
		
		return null;
	}

}
