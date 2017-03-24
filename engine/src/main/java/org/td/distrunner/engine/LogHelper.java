package org.td.distrunner.engine;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.td.distrunner.model.AppSettings;

import com.google.gson.Gson;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LogHelper {

	private static final Logger LOG = LoggerFactory.getLogger("DistRunner");
	public static final byte INFO = 0;
	public static final byte ERROR = 1;
	public static final byte TRACE = 2;

	public static void setupLog() {
		// for disable jetty server debug messages
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();
		try {
			jc.doConfigure(AppSettings.ApplicationLogSettingsPath);
		} catch (JoranException e) {
		}
	}

	private static void log(String message, byte logType) {
		switch (logType) {
		case INFO:
			LOG.info(message);
			break;
		case ERROR:
			LOG.error(message);
			break;
		case TRACE:
			LOG.trace(message);
			break;
		default:
			break;
		}
	}

	public static void logError(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		log(errors.toString(), ERROR);
	}
	
	public static void logError(Throwable e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		log(errors.toString(), ERROR);
	}
	
	public static void logTrace(Object message) {
		Gson gson = new Gson();
		log(gson.toJson(message), TRACE);
	}
}
