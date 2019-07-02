package com.invengo.lib.util;

import com.invengo.lib.diagnostics.InvengoLog;

public class StringUtil {

	private static final String TAG = StringUtil.class.getSimpleName();

	// Padding Left String
	public static String padLeft(String str, int length, char pad) {
		if (str.length() >= length)
			return str;
		try {
			int padLen = length - str.length();
			for (int i = 0; i < padLen; i++) {
				str = pad + str;
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. padLeft([%s], %d, [%c]) - Failed to pad left", str, length, pad);
			return str;
		}
		return str;
	}

	// Padding Right String
	public static String padRight(String str, int length, char pad) {
		if (str.length() >= length)
			return str;

		try {
			int padLen = length - str.length();
			for (int i = 0; i < padLen; i++) {
				str += pad;
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. padRight([%s], %d, [%c]) - Failed to pad right", str, length, pad);
			return str;
		}
		return str;
	}

	// get boolean array
	public static String getBoolArray(boolean[] array) {
		String str = "";

		try {
			for (boolean item : array) {
				if (!str.isEmpty())
					str += ", ";
				str += item;
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. getBooleanArray() - Failed to get boolean array string");
			return "";
		}
		return str;
	}

	// get integer array
	public static String getIntArray(int[] array) {
		if (array == null)
			return "";

		StringBuilder builder = new StringBuilder();
		try {
			for (int item : array) {
				if (builder.length() > 0)
					builder.append(", ");
				builder.append(item);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. getIntArray() - Failed to get integer array string");
			return "";
		}
		return builder.toString();
	}
	
	// Get string array
	public static String getStringArray(String[] array) {
		if (array == null)
			return "";

		StringBuilder builder = new StringBuilder();
		try {
			for (String item : array) {
				if (builder.length() > 0)
					builder.append(", ");
				builder.append(item);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. getStringArray() - Failed to get string array string");
			return "";
		}
		return builder.toString();
	}

	// String dump
	public static String dump(String data) {
		return data.replaceAll("\000", "<NUL>").replaceAll("\001", "<SOH>").replaceAll("\002", "<STX>")
				.replaceAll("\003", "<ETX>").replaceAll("\004", "<EOT>").replaceAll("\005", "<ENQ>")
				.replaceAll("\006", "<ACK>").replaceAll("\007", "<BEL>").replaceAll("\010", "<BS>")
				.replaceAll("\011", "<HT>").replaceAll("\012", "<LF>").replaceAll("\013", "<VT>")
				.replaceAll("\014", "<FF>").replaceAll("\015", "<CR>").replaceAll("\016", "<SO>")
				.replaceAll("\017", "<SI>").replaceAll("\020", "<DLE>").replaceAll("\021", "<DC1>")
				.replaceAll("\022", "<DC2>").replaceAll("\023", "<DC3>").replaceAll("\024", "<DC4>")
				.replaceAll("\025", "<NAK>").replaceAll("\026", "<SYN>").replaceAll("\027", "<ETB>")
				.replaceAll("\030", "<CAN>").replaceAll("\031", "<EM>").replaceAll("\032", "<SUB>")
				.replaceAll("\033", "<ESC>").replaceAll("\034", "<FS>").replaceAll("\035", "<GS>")
				.replaceAll("\036", "<RS>").replaceAll("\037", "<US>");
	}
	
	// String Is Null Or Empty
	public static boolean isNullOrEmpty(String value) {
		return (value == null || value.length() == 0);
	}
	
	// Get no empty string
	public static String toString(String value) {
		return toString(value, "");
	}
	
	public static String toString(String value, String rep) {
		if (value == null)
			return rep;
		return value;
	}
}
