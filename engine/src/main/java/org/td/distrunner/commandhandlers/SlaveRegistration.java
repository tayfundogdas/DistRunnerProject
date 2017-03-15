package org.td.distrunner.commandhandlers;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.td.distrunner.model.AppSettings;

public class SlaveRegistration {

	public static void initHeartBeatJob() throws Exception {

		JobDetail job = JobBuilder.newJob(SlaveHeartBeatJob.class).withIdentity("SlaveHeartBeatJob", "group1").build();

		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("SlaveHeartBeatJobTrigger", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule(AppSettings.SlaveHeartBeatCronSchedule)).build();

		// schedule it
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);
	}
}
