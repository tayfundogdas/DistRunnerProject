package org.td.examples.stringprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.td.processmodel.CodeAction;
import org.td.typesystem.TypeConverter;
import org.td.typesystem.TypeConverter.TypeCode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SplitWordsAction implements CodeAction {

	@Override
	public List<Byte> Execute(List<Byte> input) throws Exception {
		String content = (String) TypeConverter.fromBytes(input, TypeCode.STRING);
		StringTokenizer st = new StringTokenizer(content);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreElements()) {
			tokens.add(st.nextToken());
		}

		Gson gson = new GsonBuilder().create();
		return TypeConverter.toBytes(gson.toJson(tokens), TypeCode.STRING);
	}

	@Override
	public Boolean ValidateInput(List<Byte> input) throws Exception {
		return true;
	}

}
