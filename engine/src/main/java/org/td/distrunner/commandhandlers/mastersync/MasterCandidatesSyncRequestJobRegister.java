package org.td.distrunner.commandhandlers.mastersync;

import org.td.distrunner.commandhandlers.IJobRegister;
import org.td.distrunner.engine.JobRegisterHelper;
import org.td.distrunner.model.AppSettings;

public class MasterCandidatesSyncRequestJobRegister implements IJobRegister {

	@Override
	public void startJob() throws Exception {
		JobRegisterHelper.startJob(getJobName(), MasterCandidatesSyncRequestJob.class,
				AppSettings.MasterCandidatesSyncRequestJobCronSchedule);
	}

	@Override
	public void stopJob() throws Exception {
		JobRegisterHelper.stopJob(getJobName());
	}

	@Override
	public String getJobName() {
		return "MasterCandidatesSyncRequestJob";
	}

}
