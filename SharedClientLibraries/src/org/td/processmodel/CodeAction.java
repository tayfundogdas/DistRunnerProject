package org.td.processmodel;

public interface CodeAction<I, O> {

	public O Execute(I input) throws Exception;

	public Boolean ValidateInput(I input) throws Exception;

}
