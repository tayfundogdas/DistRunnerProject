package org.td.samples.stringprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.td.processmodel.CodeAction;

public class TokenizeWordsAction implements CodeAction<String, List<String>> {

	@Override
	public List<String> Execute(String input) throws Exception {
		StringTokenizer st = new StringTokenizer(input);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreElements()) {
			tokens.add(st.nextToken());
		}
		return tokens;
	}

	@Override
	public Boolean ValidateInput(String input) throws Exception {
		return true;
	}

}
