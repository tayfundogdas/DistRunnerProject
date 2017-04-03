package org.td.samples.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.td.processmodel.CodeAction;
import org.td.samples.CalculateWordCountAction;
import org.td.samples.DownloadArticleAction;
import org.td.samples.TokenizeWordsAction;
import com.fasterxml.jackson.core.type.TypeReference;

public class StringProcessorTest extends CodeAction<String, Integer> {

	@SuppressWarnings("rawtypes")
	private List<CodeAction> ProcessTree = new ArrayList<CodeAction>();

	@Before
	public void setUp() throws Exception {
		ProcessTree.add(new DownloadArticleAction());
		ProcessTree.add(new TokenizeWordsAction());
		ProcessTree.add(new CalculateWordCountAction());
	}

	@After
	public void tearDown() throws Exception {
		ProcessTree.clear();
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public String Execute(String jsonInput) throws Exception {
		//String input = this.readInputString(jsonInput);
		String currInput = null;
		CodeAction currAction = null;
		for (int i = 0; i < ProcessTree.size(); ++i) {
			if (i == 0)
				currInput = jsonInput;
			currAction = ProcessTree.get(i);
			if (currAction.ValidateInput(currInput)) {
				currInput = currAction.Execute(currInput);
			}
		}

		return currInput;
	}

	@Override
	public Boolean ValidateInput(String input) throws Exception {
		return true;
	}

	@Test
	public void test() throws Exception {
		String firstInput = this.mapper.writeValueAsString("https://en.wikipedia.org/wiki/Java");
		Hashtable<String, Integer> finalOutput = null;
		try {
			finalOutput = this.mapper.readValue(this.Execute(firstInput), new TypeReference<Hashtable<String, Integer>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (finalOutput == null)
			fail("Finished with no result");
		else
			assertTrue(finalOutput.size() > 0);
	}

}
