package org.td.distrunner.commandhandlers.mastersync;

import org.td.distrunner.commandhandlers.IJobRegister;
import org.td.distrunner.helpers.JobRegisterHelper;
import org.td.distrunner.model.AppSettings;

public class MasterCandidatesSyncRequestJobRegister implements IJobRegister {

	// register and unregister job if this node marked with IsMasterCandidate
	@Override
	public void startJob() throws Exception {
		if (AppSettings.IsMasterCandidate == 1) {
			JobRegisterHelper.startJob(getJobName(), MasterCandidatesSyncRequestJob.class,
					AppSettings.MasterCandidatesSyncRequestJobCronSchedule);
		}
	}

	@Override
	public void stopJob() throws Exception {
		if (AppSettings.IsMasterCandidate == 1) {
			JobRegisterHelper.stopJob(getJobName());
		}
	}

	@Override
	public void restartJob() throws Exception {
		if (AppSettings.IsMasterCandidate == 1) {
			JobRegisterHelper.restartJob(getJobName());
		}
	}

	@Override
	public String getJobName() {
		return "MasterCandidatesSyncRequestJob";
	}

}
