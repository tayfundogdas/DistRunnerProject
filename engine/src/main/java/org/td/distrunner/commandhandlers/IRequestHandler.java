package org.td.distrunner.commandhandlers;

import org.td.distrunner.model.Message;

public interface IRequestHandler<I, O> {
	Message<O> handle(Message<I> message);
}
