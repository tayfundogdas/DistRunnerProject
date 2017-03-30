package org.td.distrunner.model;

import java.io.File;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Message {
	public int MessageType;
	public String MessageContent;

	@Override
	public String toString() {
		return JsonHelper.getJsonString(this);
	}

	public static Message getMessagefromString(String message) {
		Message msgObj = null;
		try {
			msgObj = JsonHelper.mapper.readValue(message, new TypeReference<Message>() {
			});
		} catch (Exception e) {
			LogHelper.logError(e);
		}
		return msgObj;
	}

	public static void main(String[] args) throws Exception {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		InMemoryObjects.AppId = uuid.toString();

		// for logging
		LogHelper.setupLog();

		List<String> lines = FileUtils.readLines(new File("D:\\input.txt"));
		for (String str : lines) {
			Message message = Message.getMessagefromString(str);
		}
	}

}
