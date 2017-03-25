package org.td.samples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.td.processmodel.CodeAction;

public class DownloadArticleAction implements CodeAction<String, String> {

	@Override
	public String Execute(String input) throws Exception {
		Document doc = Jsoup.connect(input).get();
		return doc.body().text();
	}

	@Override
	public Boolean ValidateInput(String input) throws Exception {
		if (input.startsWith("https://"))
			return true;

		return false;
	}

}
