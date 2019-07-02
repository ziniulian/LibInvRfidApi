package com.invengo.lib.util;

import java.nio.ByteOrder;
import java.util.Locale;

import com.invengo.lib.diagnostics.InvengoLog;

public class HexUtil {

	private static final String TAG = HexUtil.class.getSimpleName();

	// Output Hex Dump String
	public static String dump(byte[] buf) {
		if (buf == null)
			return "";
		return dump(buf, 0, buf.length);
	}

	public static String dump(byte[] buf, int size) {
		if (buf == null)
			return "";
		return dump(buf, 0, size);
	}

	public static String dump(byte[] buf, int start, int size) {
		if (buf == null)
			return "";

		StringBuilder builder = new StringBuilder();

		try {
			for (int i = start; i < start + size; i++) {
				builder.append(String.format(Locale.US, (i == start ? "%02X" : " %02X"), buf[i]));
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. dump(%d, %d) - Failed to dump byte array", start, size);
			return "";
		}
		return builder.toString();
	}

	public static String dump(int[] buf) {
		if (buf == null)
			return "";
		return dump(buf, 0, buf.length);
	}

	public static String dump(int[] buf, int size) {
		if (buf == null)
			return "";
		return dump(buf, 0, size);
	}

	public static String dump(int[] buf, int start, int size) {
		if (buf == null)
			return "";

		StringBuilder builder = new StringBuilder();

		try {
			for (int i = start; i < start + size; i++) {
				builder.append(String.format(Locale.US, (i == start ? "%d" : ", %d"), buf[i]));
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. dump(%d, %d) - Failed to dump byte array", start, size);
			return "";
		}
		return builder.toString();
	}

	// Convert Byte Array To Hex String
	public static String toHexString(byte[] buf) {
		if (buf == null)
			return "";
		return toHexString(buf, 0, buf.length);
	}

	public static String toHexString(byte[] buf, int size) {
		if (buf == null)
			return "";
		return toHexString(buf, 0, size);
	}

	public static String toHexString(byte[] buf, int start, int size) {
		if (buf == null)
			return "";

		StringBuilder builder = new StringBuilder();

		try {
			for (int i = start; i < start + size; i++) {
				builder.append(String.format(Locale.US, "%02X", buf[i]));
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toHexString([%s], %d, %d) - Failed to convert byte array",
					HexUtil.dump(buf, start, size), start, size);
			return "";
		}
		return builder.toString();
	}

	// Convert Byte Array To Char String
	public static String toCharString(byte[] buf) {
		if (buf == null)
			return "";
		return toCharString(buf, 0, buf.length);
	}

	public static String toCharString(byte[] buf, int size) {
		if (buf == null)
			return "";
		return toCharString(buf, 0, size);
	}

	public static String toCharString(byte[] buf, int start, int size) {
		if (buf == null)
			return "";

		StringBuilder builder = new StringBuilder();

		try {
			for (int i = start; i < start + size; i++) {
				builder.append(String.format(Locale.US, "%c", buf[i]));
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toCharString([%s], %d, %d) - Failed to convert byte array",
					HexUtil.dump(buf, start, size), start, size);
			return "";
		}
		return builder.toString();
	}

	// Convert Hex String To Byte Array
	public static byte[] toBytes(String hex) throws Exception{
		int len = 0;
		byte[] data = null;

		try {
			len = (int) (Math.ceil((double) hex.length() / 2.0));
			hex = StringUtil.padRight(hex, len * 2, '0');
			data = new byte[len];
			for (int i = 0; i < len; i++) {
				data[i] = (byte) Integer.parseInt(hex.substring(i * 2, (i * 2) + 2), 16);
			}
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toBytes([%s]) - Failed to convert hex string",
					(hex == null ? "" : hex));
			//return null;
			throw e;
		}
		return data;
	}

	// Convert Hex String To Integer
	public static int toInteger(String hex) throws Exception {
		return toInteger(hex, 0, ByteOrder.LITTLE_ENDIAN);
	}

	public static int toInteger(String hex, ByteOrder order) throws Exception {
		return toInteger(hex, 0, order);
	}

	public static int toInteger(String hex, int pos) throws Exception {
		return toInteger(hex, pos, ByteOrder.LITTLE_ENDIAN);
	}

	public static int toInteger(String hex, int pos, ByteOrder order) throws Exception {
		int value = 0;
		try {
			byte[] buf = toBytes(hex);
			value = BitConvert.toInteger(buf, pos, ByteOrder.BIG_ENDIAN);
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toInteger([%s], %d, %s) - Failed to convert hex string ",
					(hex == null ? "" : hex), pos, order);
			throw e;
		}
		return value;
	}

	// Convert Hex String To Short
	public static short toShort(String hex) throws Exception {
		return toShort(hex, 0, ByteOrder.LITTLE_ENDIAN);
	}

	public static short toShort(String hex, ByteOrder order) throws Exception {
		return toShort(hex, 0, order);
	}

	public static short toShort(String hex, int pos) throws Exception {
		return toShort(hex, pos, ByteOrder.LITTLE_ENDIAN);
	}

	public static short toShort(String hex, int pos, ByteOrder order) throws Exception {
		short value = 0;
		try {
			byte[] buf = toBytes(hex);
			value = BitConvert.toShort(buf, pos, ByteOrder.BIG_ENDIAN);
		} catch (Exception e) {
			InvengoLog.e(TAG, e, "ERROR. toShort([%s], %d, %s)", (hex == null ? "" : hex), pos, order);
			throw e;
		}
		return value;
	}
}
