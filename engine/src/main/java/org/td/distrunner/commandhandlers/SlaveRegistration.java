package org.td.distrunner.commandhandlers;

import org.td.distrunner.engine.App;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

public class SlaveRegistration {
	
	public static void Register()
	{
        WebSocketClientChannel wsClient = new WebSocketClientChannel();
        Message regMessage= new Message();
        regMessage.MessageType = MessageTypes.RegMessage;
        regMessage.MessageObject = App.AppId;
        wsClient.sendMessage(wsClient.getMasterWSAddress(), regMessage);
	}
	
	public static void pushHeartBeat()
	{
		
	}
}
