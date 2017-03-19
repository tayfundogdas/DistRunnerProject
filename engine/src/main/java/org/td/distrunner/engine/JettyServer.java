package org.td.distrunner.engine;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.td.distrunner.apirelated.ApiHandler;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.wsrelated.WebSocketServletChannel;

import ch.qos.logback.access.jetty.RequestLogImpl;

public class JettyServer {
	
	public static void startServer()
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
		
		//logging
		RequestLogImpl requestLog = new RequestLogImpl();
		requestLog.setResource("D:\\logback-access.xml");
		server.setRequestLog(requestLog);

		try {
			server.start();
			//server.join(); no wait until server is ready
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}
	
}
