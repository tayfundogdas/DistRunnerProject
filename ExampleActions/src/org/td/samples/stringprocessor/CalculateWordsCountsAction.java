package org.td.samples.stringprocessor;

import java.lang.reflect.Type;
import java.util.List;
import org.td.processmodel.CodeAction;
import org.td.typesystem.TypeConverter;
import org.td.typesystem.TypeConverter.TypeCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class CalculateWordsCountsAction implements CodeAction {

	List<String> tokens = null;

	@Override
	public List<Byte> Execute(List<Byte> input) throws Exception {
		String content = (String) TypeConverter.fromBytes(input, TypeCode.STRING);
		Gson gson = new GsonBuilder().create();
		Type listType = new TypeToken<List<String>>(){}.getType();
		tokens = gson.fromJson(content, listType);
		for (String token : tokens) {
			System.out.println(token + ":" + token.length());
		}
		return null;
	}

	@Override
	public Boolean ValidateInput(List<Byte> input) throws Exception {
		return true;
	}

}
