package org.td.distrunner.commandhandlers;

import org.td.distrunner.commandhandlers.assignmaster.NewMasterHandle;
import org.td.distrunner.commandhandlers.heartbeat.HeartBeatRequestHandle;
import org.td.distrunner.commandhandlers.mastersync.MasterCandidatesSyncRequestHandle;
import org.td.distrunner.commandhandlers.mastersync.MasterCandidatesSyncResponseHandle;
import org.td.distrunner.commandhandlers.workschedule.ExecutionRequestHandle;
import org.td.distrunner.commandhandlers.workschedule.ExecutionResultHandle;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;

public class MessageDispatcher<I, O> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Message HandleMessage(String incomingMessageText) {
		Message message = (Message) JsonHelper.fromJson(incomingMessageText, Message.class);
		IRequestHandler messageHandler = null;

		switch (message.MessageType) {
		case MessageTypes.HeartBeatRequestMessage:
			messageHandler = new HeartBeatRequestHandle();
			break;
		case MessageTypes.MasterSyncRequestMessage:
			messageHandler = new MasterCandidatesSyncRequestHandle();
			break;
		case MessageTypes.MasterSyncResponseMessage:
			messageHandler = new MasterCandidatesSyncResponseHandle();
			break;
		case MessageTypes.NewMasterMessage:
			messageHandler = new NewMasterHandle();
			break;
		case MessageTypes.ExecutionRequestMessage:
			messageHandler = new ExecutionRequestHandle();
			break;
		case MessageTypes.ExecutionResultMessage:
			messageHandler = new ExecutionResultHandle();
			break;
		default:
			messageHandler = null;
			break;
		}

		if (messageHandler != null)
			return messageHandler.handle(message);
		else
			return null;
	}
}
