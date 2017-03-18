package org.td.distrunner.commandhandlers;

public interface IJobRegister {
	 void startJob() throws Exception;
	 void stopJob() throws Exception;
	 String getJobName();
}
