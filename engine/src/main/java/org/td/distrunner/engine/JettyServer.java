package org.td.distrunner.engine;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.td.distrunner.apirelated.ApiHandler;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.wsrelated.WebSocketServletChannel;

import ch.qos.logback.access.jetty.RequestLogImpl;

public class JettyServer {

	public static void startServer() {
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(AppSettings.JettyPort);
		server.addConnector(connector);
		
		//jetty handlers collection
		HandlerCollection handlers = new HandlerCollection();
		server.setHandler(handlers);

		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		ServletContextHandler servletRequestHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletRequestHandler.setContextPath("/");
		handlers.addHandler(servletRequestHandler);

		// Add a websocket to a specific path spec
		ServletHolder holderEvents = new ServletHolder(AppSettings.WSChannelName, WebSocketServletChannel.class);
		servletRequestHandler.addServlet(holderEvents, "/" + AppSettings.WSChannelName + "/*");

		// add a api path
		ServletHolder api = new ServletHolder(AppSettings.APIChannelName, ApiHandler.class);
		servletRequestHandler.addServlet(api, "/" + AppSettings.APIChannelName + "/*");

		// request logging
		RequestLogImpl reqLogImpl = new RequestLogImpl();
		reqLogImpl.setFileName(AppSettings.RequestLogSettingsPath);
		reqLogImpl.start();

		RequestLogHandler regLogHandler = new RequestLogHandler();
		regLogHandler.setRequestLog(reqLogImpl);
		handlers.addHandler(regLogHandler);
		
		try {
			server.start();
			// server.join(); no wait until server is ready
		} catch (Exception e) {
			LogHelper.logError(e);
		}
	}

}
