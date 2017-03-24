package org.td.samples.stringprocessor;

import java.util.Hashtable;
import java.util.List;
import org.td.processmodel.CodeAction;

public class CalculateWordsCountsAction implements CodeAction<List<String>, Hashtable<String, Integer>> {

	@Override
	public Hashtable<String, Integer> Execute(List<String> input) throws Exception {
		Hashtable<String, Integer> result = new Hashtable<String, Integer>();
		for (String token : input) {
			result.put(token, token.length());
		}
		return result;
	}

	@Override
	public Boolean ValidateInput(List<String> input) throws Exception {
		return true;
	}

}
