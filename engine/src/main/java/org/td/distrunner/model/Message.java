package org.td.distrunner.model;

import com.google.gson.Gson;

public class Message {

	public int MessageType;
	public Object MessageObject;

	public String getJsonForm() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	@Override
	public String toString() {
		return getJsonForm();
	}

}
