package org.td.samples.stringprocessor;

import java.util.ArrayList;
import java.util.List;

import org.td.processmodel.CodeAction;
import org.td.typesystem.TypeConverter;
import org.td.typesystem.TypeConverter.TypeCode;

//from ProcessNodesfromXMLoperation some execution model created
//like below
public class StringProcessorProcess implements CodeAction {
	private static List<CodeAction> ProcessTree = new ArrayList<CodeAction>();

	// for test only!! normally it will be created from
	// ProcessNodesfromXMLoperation
	public void InitProcessTree() {
		ProcessTree.add(new DownloadArticleAction());
		ProcessTree.add(new TokenizeWordsAction());
		ProcessTree.add(new CalculateWordsCountsAction());
	}

	@Override
	public List<Byte> Execute(List<Byte> input) throws Exception {

		InitProcessTree();

		List<Byte> currInput = null;
		CodeAction currAction = null;
		for (int i = 0; i < ProcessTree.size(); ++i) {
			if (i == 0)
				currInput = input;
			currAction = ProcessTree.get(i);
			if (currAction.ValidateInput(currInput)) {
				currInput = currAction.Execute(currInput);
			}
		}

		return currInput;
	}

	@Override
	public Boolean ValidateInput(List<Byte> input) throws Exception {
		return true;
	}

	// test method
	public static void main(String[] args) throws Exception {
		CodeAction process = new StringProcessorProcess();
		List<Byte> firstInput = TypeConverter.toBytes("https://en.wikipedia.org/wiki/Java", TypeCode.STRING);
		List<Byte> finalOutput = process.Execute(firstInput);
		if (finalOutput == null)
			System.out.println("Finished with no result");
		else
			System.out.println((String) TypeConverter.fromBytes(finalOutput, TypeCode.STRING));
	}

}
