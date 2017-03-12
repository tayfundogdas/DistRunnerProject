package org.td.typesystem;

import java.util.ArrayList;
import java.util.List;

public class TypeConverter {

	public enum TypeCode {
		STRING(30), BOOLEAN(10), INTEGER(15), DOUBLE(20);
		private int enumVal;

		private TypeCode(int pvalue) {
			this.enumVal = pvalue;
		}
	}

	public static Object fromBytes(List<Byte> input, TypeCode desiredType) {
		switch (desiredType) {
		case STRING:
			return bytesToString(input);
		default:
			return null;
		}
	}

	private static String bytesToString(List<Byte> input) {
		byte[] arr = new byte[input.size()];
		for (int i = 0; i < input.size(); ++i)
			arr[i] = input.get(i);
		return new String(arr);
	}

	public static List<Byte> toBytes(Object input, TypeCode inputType) {
		switch (inputType) {
		case STRING:
			return stringToBytes((String)input);
		default:
			return null;
		}
	}

	private static List<Byte> stringToBytes(String input) {
		byte[] arr = input.getBytes();
		List<Byte> res = new ArrayList<Byte>();
		for (int i = 0; i < arr.length; ++i)
			res.add(arr[i]);

		return res;
	}

}
