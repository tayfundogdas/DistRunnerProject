package org.td.distrunner.engine;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.td.distrunner.apirelated.ApiHandler;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.wsrelated.WebSocketServletChannel;

public class JettyServer {
	
	public void startServer()
	{
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(AppSettings.JettyPort);
		server.addConnector(connector);

		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		// Add a websocket to a specific path spec
		ServletHolder holderEvents = new ServletHolder(AppSettings.WSChannelName, WebSocketServletChannel.class);
		context.addServlet(holderEvents, "/" + AppSettings.WSChannelName + "/*");
		
		// add a api path
		ServletHolder api = new ServletHolder(AppSettings.APIChannelName, ApiHandler.class);
		context.addServlet(api, "/" + AppSettings.APIChannelName + "/*");

		try {
			server.start();
			server.dump(System.err);
			server.join();
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
	
}
