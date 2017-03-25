package org.td.distrunner.model;

import com.google.gson.Gson;

public class Message <T> {

	public int MessageType;
	public T MessageContent;

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static String toJsonString(Object obj)
	{
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

}
