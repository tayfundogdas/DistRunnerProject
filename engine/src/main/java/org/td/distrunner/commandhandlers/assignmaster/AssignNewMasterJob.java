package org.td.distrunner.commandhandlers.assignmaster;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.ClientModel;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import org.td.distrunner.wsrelated.WebSocketClientChannel;

// assign new master among master candidates stated in settings
public class AssignNewMasterJob {

	private static ScheduledFuture<?> broadcastNewMasterMessagetoNodeshandle;

	// in case of heartBeats fails for threshold broadcast
	// new master message according to jobcount*waittimeconstant
	// to all nodes
	public static void broadcastNewMasterMessagetoNodes() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		long delay = 0;
		ClientModel myJobCount = InMemoryObjects.clients.get(InMemoryObjects.AppId);
		if (myJobCount == null || myJobCount.JobCount <= 0)
			delay = AppSettings.MasterRequestWaitTimeConstant;
		else
			delay = myJobCount.JobCount * AppSettings.MasterRequestWaitTimeConstant;

		broadcastNewMasterMessagetoNodeshandle = scheduler.schedule(new Runnable() {
			public void run() {
				InMemoryObjects.clients.forEach(Runtime.getRuntime().availableProcessors(), (clientId, client) -> {
					Message mess = new Message();
					mess.MessageType = MessageTypes.NewMasterMessage;
					// send my ip as new master address
					mess.MessageObject = InMemoryObjects.clients.get(InMemoryObjects.AppId).Address;
					try {
						WebSocketClientChannel.sendMessagetoAddress(mess, client.Address);
					} catch (Exception e) 
					{
						LogHelper.logError(e);
					}
				});
			}
		}, delay, TimeUnit.SECONDS);
	}

	// if new master message already taken cancel my request
	public static void cancelMyNewMasterMessage() {
		if (broadcastNewMasterMessagetoNodeshandle != null)
			broadcastNewMasterMessagetoNodeshandle.cancel(true);
	}

}
