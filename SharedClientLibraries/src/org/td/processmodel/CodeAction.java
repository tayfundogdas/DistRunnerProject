package org.td.processmodel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class CodeAction<I, O> {

	protected ObjectMapper mapper = new ObjectMapper();

	public abstract String Execute(String jsonInput) throws Exception;

	public abstract Boolean ValidateInput(String jsonInput) throws Exception;

	protected String convertResultToString(O output) throws Exception {
		return mapper.writeValueAsString(output);
	}

	protected I readInputString(String jsonInput) throws Exception {
		return this.mapper.readValue(jsonInput, new TypeReference<I>() {
		});
	}

}
