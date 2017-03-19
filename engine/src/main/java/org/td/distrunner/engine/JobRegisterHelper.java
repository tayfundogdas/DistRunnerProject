package org.td.distrunner.engine;

import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;
import org.td.distrunner.commandhandlers.IJobRegister;

public class JobRegisterHelper {

	private static Scheduler getScheduler() throws Exception {
		return new StdSchedulerFactory().getScheduler();
	}

	private static JobDetail getJobByName(String jobName, Class<? extends Job> jobClass) {
		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName).build();
		return job;
	}

	private static Trigger getTriggerforJobByName(String jobName, String cronExpression) {
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

		return trigger;
	}

	public static void registerJobs() throws Exception {
		Reflections reflections = new Reflections("org.td.distrunner.commandhandlers");
		Set<Class<? extends IJobRegister>> classes = reflections.getSubTypesOf(IJobRegister.class);
		classes.forEach(clazz -> {
			try {
				final IJobRegister registerHandle = clazz.newInstance();
				registerHandle.startJob();
			} catch (final Exception e) {
				LogHelper.logError(e);
			}
		});
	}

	public static void startScheduler() throws Exception {
		getScheduler().start();
	}

	public static void startJob(String jobName, Class<? extends Job> jobClass, String cronExpression) throws Exception {
		// put scheduler entry
		Scheduler scheduler = getScheduler();
		JobDetail job = getJobByName(jobName, jobClass);
		Trigger trigger = JobRegisterHelper.getTriggerforJobByName(jobName, cronExpression);
		scheduler.scheduleJob(job, trigger);
	}

	public static void stopJob(String jobName) throws Exception {
		Scheduler scheduler = getScheduler();
		scheduler.pauseTrigger(new TriggerKey(jobName));
	}
	
	public static void cancelAllJobsIfMasterDied()
	{
		Reflections reflections = new Reflections("org.td.distrunner.commandhandlers");
		Set<Class<? extends IJobRegister>> classes = reflections.getSubTypesOf(IJobRegister.class);
		classes.forEach(clazz -> {
			try {
				final IJobRegister registerHandle = clazz.newInstance();
				registerHandle.stopJob();
			} catch (final Exception e) {
				LogHelper.logError(e);
			}
		});
	}
	
	public static void restartJob(String jobName) throws Exception {
		Scheduler scheduler = getScheduler();
		scheduler.resumeTrigger(new TriggerKey(jobName));
	}
	
	public static void restartAllJobsIfMasterUp()
	{
		Reflections reflections = new Reflections("org.td.distrunner.commandhandlers");
		Set<Class<? extends IJobRegister>> classes = reflections.getSubTypesOf(IJobRegister.class);
		classes.forEach(clazz -> {
			try {
				final IJobRegister registerHandle = clazz.newInstance();
				registerHandle.restartJob();
			} catch (final Exception e) {
				LogHelper.logError(e);
			}
		});
	}
}
