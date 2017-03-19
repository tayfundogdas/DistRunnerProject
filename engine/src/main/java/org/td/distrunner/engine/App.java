package org.td.distrunner.engine;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {
	

	public static void main(String[] args) {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		InMemoryObjects.AppId = uuid.toString();

		// for logging
		LogHelper.setupLog();

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
