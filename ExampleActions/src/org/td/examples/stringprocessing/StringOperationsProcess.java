package org.td.examples.stringprocessing;

import java.util.ArrayList;
import java.util.List;

import org.td.processmodel.CodeAction;

//from ProcessNodesfromXMLoperation soem execution model created
//like below
public class StringOperationsProcess {
	private static List<CodeAction> ProcessTree = new ArrayList<CodeAction>();
	
	public void InitProcessTree()
	{
		ProcessTree.add(new DownloadArticleAction());
		ProcessTree.add(new SplitWordsAction());
		ProcessTree.add(new PrintCharCountAction());
	}
}
