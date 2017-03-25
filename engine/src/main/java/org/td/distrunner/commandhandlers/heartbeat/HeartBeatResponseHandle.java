package org.td.distrunner.commandhandlers.heartbeat;

import java.lang.reflect.Type;
import java.util.List;
import org.td.distrunner.commandhandlers.IRequestHandler;
import org.td.distrunner.commandhandlers.clientexecutor.ExecuteJob;
import org.td.distrunner.model.ClientJobModel;
import org.td.distrunner.model.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HeartBeatResponseHandle implements IRequestHandler<String,Object> {

	@Override
	public Message<Object> handle(Message<String> message) {
		Type listType = new TypeToken<List<ClientJobModel>>() {}.getType();
		Gson gson = new Gson();
		List<ClientJobModel> jobs = gson.fromJson(message.MessageContent, listType);
		ExecuteJob.executeJobsAndReportResulttoMaster(jobs);

		return null;
	}

}
