package org.td.samples;

import org.td.processmodel.CodeAction;

public class CalculateWordCountAction extends CodeAction<String, Integer> {

	@Override
	public String Execute(String jsonInput) throws Exception {
		String input = this.readInputString(jsonInput);
		return this.convertResultToString(input.length());
	}

	@Override
	public Boolean ValidateInput(String jsonInput) throws Exception {
		return true;
	}

}
