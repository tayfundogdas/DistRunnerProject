package org.td.distrunner.commandhandlers.clientexecutor;

import org.td.distrunner.commandhandlers.IJobRegister;
import org.td.distrunner.helpers.JobRegisterHelper;
import org.td.distrunner.model.AppSettings;

public class ExecuteJobRegister implements IJobRegister {

	@Override
	public void startJob() throws Exception {
		JobRegisterHelper.startJob(getJobName(), ExecuteJob.class,
				AppSettings.NodeExecuteJobCronSchedule);
	}

	@Override
	public void stopJob() throws Exception {
		JobRegisterHelper.stopJob(getJobName());
	}

	@Override
	public void restartJob() throws Exception {
		JobRegisterHelper.restartJob(getJobName());		
	}
	
	@Override
	public String getJobName() {
		return "ExecuteJob";
	}
}
