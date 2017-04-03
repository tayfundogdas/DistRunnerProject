package org.td.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.td.processmodel.CodeAction;

public class TokenizeWordsAction extends CodeAction<String, List<String>> {

	@Override
	public String Execute(String jsonInput) throws Exception {
		String input = this.readInputString(jsonInput);
		StringTokenizer st = new StringTokenizer(input);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreElements()) {
			tokens.add(st.nextToken());
		}
		return this.convertResultToString(tokens);
	}

	@Override
	public Boolean ValidateInput(String jsonInput) throws Exception {
		return true;
	}

}
