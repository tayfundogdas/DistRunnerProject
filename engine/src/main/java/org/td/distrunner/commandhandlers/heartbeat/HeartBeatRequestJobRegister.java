package org.td.distrunner.commandhandlers.heartbeat;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.td.distrunner.commandhandlers.IJobRegister;
import org.td.distrunner.model.AppSettings;

public class HeartBeatRequestJobRegister implements IJobRegister {

	@Override
	public void registerJob() throws Exception {
		// request heartbeat to master if this node is not master
		if (!AppSettings.MasterAddress.isEmpty()) {
			JobDetail job = JobBuilder.newJob(HeartBeatRequestJob.class)
					.withIdentity("HeartBeatRequestJob", "grpHeartBeatRequestJob").build();

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("HeartBeatRequestJobTrigger", "grpHeartBeatRequestJob")
					.withSchedule(CronScheduleBuilder.cronSchedule(AppSettings.HeartBeatRequestJobCronSchedule))
					.build();

			// schedule it
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		}
	}
}
