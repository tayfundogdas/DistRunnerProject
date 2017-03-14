package org.td.distrunner.engine;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {
	public static String AppId = null;

	public static void main(String[] args) {
		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		AppId = uuid.toString();

		// start socket and api server valid both for master and slave
		JettyServer jetty = new JettyServer();
		jetty.startServer();
		
		// according to slave role register and send heartbeats with quartz interval
	}
}
