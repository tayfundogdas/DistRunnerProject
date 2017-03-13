package org.td.distrunner.engine;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App {
	public static String AppId = null;

	public static void main(String[] args) {
		System.out.println("Hello World!");

		// unique app id for tracking
		UUID uuid = UUID.randomUUID();
		AppId = uuid.toString();

		// start socket server
		JettyServer jetty = new JettyServer();
		jetty.startServer();
	}
}
