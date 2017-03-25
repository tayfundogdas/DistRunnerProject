package org.td.distrunner.commandhandlers;

import org.td.distrunner.commandhandlers.assignmaster.NewMasterHandle;
import org.td.distrunner.commandhandlers.heartbeat.HeartBeatRequestHandle;
import org.td.distrunner.commandhandlers.heartbeat.HeartBeatResponseHandle;
import org.td.distrunner.commandhandlers.mastersync.MasterCandidatesSyncRequestHandle;
import org.td.distrunner.commandhandlers.mastersync.MasterCandidatesSyncResponseHandle;
import org.td.distrunner.commandhandlers.workschedule.ExecutionResultHandle;
import org.td.distrunner.model.Message;
import org.td.distrunner.model.MessageTypes;
import com.google.gson.Gson;

public class MessageDispatcher<I, O> {

	private static Gson gson = new Gson();

	public static void manipulateHeartBeatMessage(Message<String> message, String fromAddress) {
		StringBuilder newContent = new StringBuilder(message.MessageContent);
		newContent.append('@');
		newContent.append(fromAddress);
		message.MessageContent = newContent.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Message HandleMessage(String incomingMessageText, String fromAddress) {
		Message message = gson.fromJson(incomingMessageText, Message.class);
		IRequestHandler messageHandler = null;

		switch (message.MessageType) {
		case MessageTypes.HeartBeatRequestMessage:
			messageHandler = new HeartBeatRequestHandle();
			// small hack to add incoming message fromAddress
			manipulateHeartBeatMessage(message, fromAddress);
			break;
		case MessageTypes.HeartBeatResponseMessage:
			messageHandler = new HeartBeatResponseHandle();
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
