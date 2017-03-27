package org.td.distrunner.model;

import org.td.distrunner.engine.JsonHelper;

public class Message <T> {

	public int MessageType;
	public T MessageContent;
	
	@Override
	public String toString() {
		return JsonHelper.getJsonString(this);
	}

}
