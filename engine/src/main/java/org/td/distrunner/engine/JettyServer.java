package org.td.distrunner.engine;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.td.distrunner.apirelated.ApiHandler;
import org.td.distrunner.communication.custom.ServerSocket;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.AppSettings;

import ch.qos.logback.access.jetty.RequestLogImpl;

public class JettyServer {

	public static void startServer() {
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(AppSettings.JettyPort);
		server.addConnector(connector);

		// jetty handlers collection
		HandlerCollection handlers = new HandlerCollection();
		server.setHandler(handlers);

		// add a api path
		ServletContextHandler apiServletRequestHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		apiServletRequestHandler.setContextPath("/" + AppSettings.APIChannelName);
		ServletHolder api = new ServletHolder(AppSettings.APIChannelName, ApiHandler.class);
		apiServletRequestHandler.addServlet(api, "/*");
		handlers.addHandler(apiServletRequestHandler);
		
		// Add custom communication path
		ServletContextHandler messageServletRequestHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		messageServletRequestHandler.setContextPath("/communication");
		ServletHolder message = new ServletHolder("communication", ServerSocket.class);
		messageServletRequestHandler.addServlet(message, "/*");
		handlers.addHandler(messageServletRequestHandler);

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
