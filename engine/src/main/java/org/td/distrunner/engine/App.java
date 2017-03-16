package org.td.distrunner.engine;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {
	public static String AppId = null;

	public static void main(String[] args) throws Exception {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		AppId = uuid.toString();

		// register scheduled jobs
		ScheduledJobsRegisterer.registerJobs();

		// start socket and api server valid both for master and slave
		JettyServer.startServer();

	}
}
