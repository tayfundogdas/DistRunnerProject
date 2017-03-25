package org.td.samples.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.td.processmodel.CodeAction;
import org.td.samples.CalculateWordsCountsAction;
import org.td.samples.DownloadArticleAction;
import org.td.samples.TokenizeWordsAction;

public class StringProcessorTest implements CodeAction<String, Hashtable<String, Integer>> {

	private List<CodeAction> ProcessTree = new ArrayList<CodeAction>();

	@Before
	public void setUp() throws Exception {
		ProcessTree.add(new DownloadArticleAction());
		ProcessTree.add(new TokenizeWordsAction());
		ProcessTree.add(new CalculateWordsCountsAction());
	}

	@After
	public void tearDown() throws Exception {
		ProcessTree.clear();
	}

	@Override
	public Hashtable<String, Integer> Execute(String input) throws Exception {
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

	@Test
	public void test() {
		String firstInput = "https://en.wikipedia.org/wiki/Java";
		Hashtable<String, Integer> finalOutput = null;
		try {
			finalOutput = this.Execute(firstInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (finalOutput == null)
			fail("Finished with no result");
		else
			assertTrue(finalOutput.size() > 0);
	}

}
