package org.td.distrunner.engine;

import java.util.Set;

import org.reflections.Reflections;
import org.td.distrunner.commandhandlers.IJobRegister;

public class ScheduledJobsRegisterer {

	public static void registerJobs() {
		Reflections reflections = new Reflections("org.td.distrunner.commandhandlers");
		Set<Class<? extends IJobRegister>> classes = reflections.getSubTypesOf(IJobRegister.class);
		classes.forEach(clazz -> {
			try {
				final IJobRegister registerHandle = clazz.newInstance();
				registerHandle.registerJob();
			} catch (final Exception e) {
				e.printStackTrace(System.err);
			}
		});
	}
}
