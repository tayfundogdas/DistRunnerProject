package org.td.distrunner.engine;

import java.util.UUID;

import org.td.distrunner.commandhandlers.SlaveRegistration;

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

		// register and send heartbeats with quartz interval
		SlaveRegistration.initHeartBeatJob();

		// start socket and api server valid both for master and slave
		JettyServer jetty = new JettyServer();
		jetty.startServer();

	}
}
