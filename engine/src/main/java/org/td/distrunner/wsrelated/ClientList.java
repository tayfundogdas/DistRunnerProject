package org.td.distrunner.wsrelated;

import java.util.concurrent.ConcurrentHashMap;

import org.td.distrunner.engine.LogHelper;

public class ClientList {
	private static final ClientList INSTANCE = new ClientList();

	public static ClientList getInstance() {
		return INSTANCE;
	}

	private ConcurrentHashMap<String, ServerSocket> members = new ConcurrentHashMap<String, ServerSocket>();

	public void join(String Id, ServerSocket socket) {
		// if (!members.containsKey(Id)) {
		members.put(Id, socket);
		LogHelper.logTrace("New client joined : " + Id);
		// }
	}

	public void part(ServerSocket socket) {
		members.remove(socket);
	}

	public void writeAllMembers(String message) {
		for (ServerSocket member : members.values()) {
			member.session.getRemote().sendStringByFuture(message);
		}
	}

	public void writeSpecificMember(String Id, String message) {
		ServerSocket member = findMemberByName(Id);
		member.session.getRemote().sendStringByFuture(message);
	}

	public ServerSocket findMemberByName(String Id) {
		LogHelper.logTrace("New request to write : " + Id);
		return members.get(Id);
	}
}
