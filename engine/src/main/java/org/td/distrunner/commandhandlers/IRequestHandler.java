package org.td.distrunner.commandhandlers;

import org.td.distrunner.model.Message;

public interface IRequestHandler {
	Message handle(Message message);
}
