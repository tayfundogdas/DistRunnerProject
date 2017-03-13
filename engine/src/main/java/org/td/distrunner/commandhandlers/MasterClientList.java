package org.td.distrunner.commandhandlers;

import java.util.ArrayList;
import java.util.List;

import org.td.distrunner.model.ClientModel;

public class MasterClientList {
	public static List<ClientModel> clients = new ArrayList<ClientModel>();
	
	public static void join(String clientUniqueId)
	{
		ClientModel client = new ClientModel();
		client.Id = clientUniqueId;
		clients.add(client);
	}
	
	public static void giveHeartBeat()
	{
		
	}
}
