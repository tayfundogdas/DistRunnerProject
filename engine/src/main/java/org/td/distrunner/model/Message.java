package org.td.distrunner.model;

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

}
