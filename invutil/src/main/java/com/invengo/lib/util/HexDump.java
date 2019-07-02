package com.invengo.lib.util;

import java.util.Locale;

public class HexDump {
	public static String dump(byte[] buf) {
		if (buf == null)
			return "[NULL]";
		return dump(buf, 0, buf.length);
	}

	public static String dump(byte[] buf, int size) {
		if (buf == null)
			return "[NULL]";
		return dump(buf, 0, size);
	}

	public static String dump(byte[] buf, int start, int size) {
		if (buf == null)
			return "[NULL]";
		
		StringBuilder builder = new StringBuilder();

		for (int i = start; i < start + size; i++) {
			builder.append(String.format(Locale.US, (i == start ? "%02X" : " %02X"), buf[i]));
		}
		return builder.toString();
	}

	public static String toHexString(byte[] buf) {
		if (buf == null)
			return "[NULL]";
		return toHexString(buf, 0, buf.length);
	}

	public static String toHexString(byte[] buf, int size) {
		if (buf == null)
			return "[NULL]";
		return toHexString(buf, 0, size);
	}

	public static String toHexString(byte[] buf, int start, int size) {
		if (buf == null)
			return "[NULL]";
		
		StringBuilder builder = new StringBuilder();

		for (int i = start; i < start + size; i++) {
			builder.append(String.format(Locale.US, "%02X", buf[i]));
		}
		return builder.toString();
	}
	
	public static String toCharString(byte[] buf, int start, int size){
		if (buf == null)
			return "[NULL]";
		
		StringBuilder builder = new StringBuilder();

		for (int i = start; i < start + size; i++) {
			builder.append(String.format(Locale.US, "%c", buf[i]));
		}
		return builder.toString();
	}
}
