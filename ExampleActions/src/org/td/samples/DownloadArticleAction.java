package org.td.samples;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.td.processmodel.CodeAction;

public class DownloadArticleAction extends CodeAction<String, String> {

	@Override
	public String Execute(String jsonInput) throws Exception {
		String input = this.readInputString(jsonInput);
		Document doc = Jsoup.connect(input).get();
		return this.convertResultToString(doc.body().text());
	}

	@Override
	public Boolean ValidateInput(String jsonInput) throws Exception {
		String input = this.readInputString(jsonInput);
		if (input.startsWith("https://"))
			return true;

		return false;
	}

}
