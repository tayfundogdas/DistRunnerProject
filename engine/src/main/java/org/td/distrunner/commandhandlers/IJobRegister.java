package org.td.distrunner.commandhandlers;

public interface IJobRegister {
	 void startJob() throws Exception;
	 void stopJob() throws Exception;
	 void restartJob() throws Exception;
	 String getJobName();
}
