package org.td.distrunner.engine;

import java.util.UUID;

import org.td.distrunner.communication.custom.ClientSocket;
import org.td.distrunner.helpers.CacheHelper;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.helpers.JobRegisterHelper;
import org.td.distrunner.helpers.LogHelper;

public class App {

	public static void main(String[] args) {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		InMemoryObjects.AppId = uuid.toString();

		// for logging
		LogHelper.setupLog();
		
		//for communication channel
		CommunicationHelper.setClientMode(new ClientSocket());

		// for loading process cache
		CacheHelper.LoadProcessCache();

		try {
			// register scheduled jobs
			JobRegisterHelper.registerJobs();
			// start jobs
			JobRegisterHelper.startScheduler();
		} catch (Exception e) {
			LogHelper.logError(e);
		}

		// start socket and api server valid both for master and slave
		JettyServer.startServer();
	}
}
