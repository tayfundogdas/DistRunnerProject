package org.td.examples;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.td.processmodel.CodeAction;
import org.td.typesystem.TypeConverter;
import org.td.typesystem.TypeConverter.TypeCode;

public class DownloadArticleAction implements CodeAction {

	String url = null;

	@Override
	public List<Byte> Execute(List<Byte> input) throws Exception {
		Document doc = Jsoup.connect(url).get();
		return TypeConverter.toBytes(doc.body().text(), TypeCode.STRING);
	}

	// sample input https://en.wikipedia.org/wiki/Java
	@Override
	public Boolean ValidateInput(List<Byte> input) throws Exception {
		url = (String) TypeConverter.fromBytes(input, TypeCode.STRING);
		if (url.startsWith("https://"))
			return true;

		return false;
	}

	// test method
	public static void main(String[] args) throws Exception {
		CodeAction action = new DownloadArticleAction();
		List<Byte> inp = TypeConverter.toBytes("https://en.wikipedia.org/wiki/Java", TypeCode.STRING);
		if (action.ValidateInput(inp))
		{
			System.out.println((String)TypeConverter.fromBytes(action.Execute(inp), TypeCode.STRING));
		}
		else
			System.out.println("Err in validation");
	}

}
