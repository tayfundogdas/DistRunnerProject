package org.td.distrunner.commandhandlers.heartbeat;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.td.distrunner.commandhandlers.assignmaster.AssignNewMasterJob;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.helpers.JobRegisterHelper;
import org.td.distrunner.model.AppSettings;

public class HeartBeatRequestJob implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// if master is up send heart beat message
		if (InMemoryObjects.heartBeatFailCount < AppSettings.HeartBeatTreshold) {
			HeartBeatClientPipe.sendHeartBeat();
		} else // if master is down stop all jobs
		{
			JobRegisterHelper.cancelAllJobsIfMasterDied();

			// if this node is master candidate signal NewMasterMessage
			if (AppSettings.IsMasterCandidate == 1) {
				AssignNewMasterJob.broadcastNewMasterMessagetoNodes();
			}
		}
	}
}
