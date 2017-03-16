package org.td.distrunner.commandhandlers.mastersync;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.td.distrunner.commandhandlers.IJobRegister;
import org.td.distrunner.model.AppSettings;

public class MasterCandidatesSyncRequestJobRegister implements IJobRegister {

	@Override
	public void registerJob() throws Exception {
		// request sync with master if it is master candidate
		if (AppSettings.IsMasterCandidate == 1) {
			JobDetail job = JobBuilder.newJob(MasterCandidatesSyncRequestJob.class)
					.withIdentity("MasterCandidatesSyncRequestJob", "grpMasterCandidatesSyncRequestJob").build();

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("MasterCandidatesSyncRequestJobTrigger", "grpMasterCandidatesSyncRequestJob")
					.withSchedule(
							CronScheduleBuilder.cronSchedule(AppSettings.MasterCandidatesSyncRequestJobCronSchedule))
					.build();

			// schedule it
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		}
	}

}
