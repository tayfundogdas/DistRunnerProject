package org.td.distrunner.commandhandlers.heartbeat;

import org.td.distrunner.commandhandlers.IJobRegister;
import org.td.distrunner.engine.JobRegisterHelper;
import org.td.distrunner.model.AppSettings;

public class HeartBeatRequestJobRegister implements IJobRegister {

	@Override
	public void startJob() throws Exception {
		JobRegisterHelper.startJob(getJobName(), HeartBeatRequestJob.class,
				AppSettings.HeartBeatRequestJobCronSchedule);
	}

	@Override
	public void stopJob() throws Exception {
		JobRegisterHelper.stopJob(getJobName());
	}

	@Override
	public String getJobName() {
		return "HeartBeatRequestJob";
	}
}
