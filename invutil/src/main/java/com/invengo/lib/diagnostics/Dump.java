package com.invengo.lib.diagnostics;

public final class Dump {

	public static String toString(byte[] data) {
		return toString(data, 0, data.length);
	}
	public static String toString(byte[] data, int length) {
		return toString(data, 0, length);
	}
	public static String toString(byte[] data, int offset, int length) {
		StringBuilder sb = new StringBuilder();
		if (offset < 0 || offset >= data.length)
			return "";
		if (offset + length > data.length)
			length = data.length - offset;

		for (int i = offset; i < length; i++) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(String.format("%02X", data[i]));
		}
		return sb.toString();
	}
}
