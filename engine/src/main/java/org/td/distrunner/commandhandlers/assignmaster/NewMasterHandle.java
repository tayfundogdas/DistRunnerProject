package org.td.distrunner.commandhandlers.assignmaster;

import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.engine.JobRegisterHelper;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;

public class NewMasterHandle implements IRequestHandler<String,Object> {

	@Override
	public Message<Object> handle(Message<String> message) {
		// cancel waiting new master operation
		AssignNewMasterJob.cancelMyNewMasterMessage();

		// assign new master address
		String newMasterAddress = message.MessageContent;
		AppSettings.MasterAddress = newMasterAddress;

		// save app.config for changed master address
		AppSettings.saveToFile();

		// start all all jobs again for changed master address
		try {
			// restart jobs
			JobRegisterHelper.restartAllJobsIfMasterUp();
		} catch (Exception e) {
			LogHelper.logError(e);
		}

		return null;
	}

}
