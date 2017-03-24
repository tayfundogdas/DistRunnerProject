package org.td.samples.stringprocessor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.td.processmodel.CodeAction;

//from ProcessNodesfromXMLoperation some execution model created
//like below
public class StringProcessorProcess implements CodeAction<String, Hashtable<String, Integer>> {
	private static List<CodeAction> ProcessTree = new ArrayList<CodeAction>();

	// for test only!! normally it will be created from
	// ProcessNodesfromXMLoperation
	public void InitProcessTree() {
		ProcessTree.add(new DownloadArticleAction());
		ProcessTree.add(new TokenizeWordsAction());
		ProcessTree.add(new CalculateWordsCountsAction());
	}

	@Override
	public Hashtable<String, Integer> Execute(String input) throws Exception {
		InitProcessTree();

		Object currInput = null;
		CodeAction currAction = null;
		for (int i = 0; i < ProcessTree.size(); ++i) {
			if (i == 0)
				currInput = input;
			currAction = ProcessTree.get(i);
			if (currAction.ValidateInput(currInput)) {
				currInput = currAction.Execute(currInput);
			}
		}

		return (Hashtable<String, Integer>) currInput;
	}

	@Override
	public Boolean ValidateInput(String input) throws Exception {
		return true;
	}

	// test method
	public static void main(String[] args) throws Exception {
		CodeAction process = new StringProcessorProcess();
		Object firstInput = "https://en.wikipedia.org/wiki/Java";
		Object finalOutput = process.Execute(firstInput);
		if (finalOutput == null)
			System.out.println("Finished with no result");
		else
			System.out.println(finalOutput);
	}
}
