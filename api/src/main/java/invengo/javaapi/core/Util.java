package invengo.javaapi.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	private static final Pattern hexPattern = Pattern.compile("^[0-9a-fA-F]+$");;

	public static String convertByteArrayToHexString(byte[] byte_array) {
		String s = "";

		if (byte_array == null)
			return s;

		for (int i = 0; i < byte_array.length; i++) {
			String hex = Integer.toHexString(byte_array[i] & 0xff);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			s = s + hex;
		}
		return s.toUpperCase();
	}

	public static String convertByteArrayToHexWordString(byte[] byte_array) {
		String s = "";

		if (byte_array == null)
			return s;

		for (int i = 0; i < byte_array.length; i += 2) {
			String hex1 = Integer.toHexString(byte_array[i] & 0xff);
			String hex2 = Integer.toHexString(byte_array[i + 1] & 0xff);
			if (hex1.length() == 1) {
				hex1 = "0" + hex1;
			}
			if (hex2.length() == 1) {
				hex2 = "0" + hex2;
			}
			s = s + hex1 + hex2;
		}
		return s;
	}

	public static String convertByteToHexWordString(byte by) {
		String hex1 = Integer.toHexString(by & 0xff);
		if (hex1.length() == 1) {
			hex1 = "0" + hex1;
		}
		return hex1.toUpperCase();
	}

	public static String convertIntToHexString(int value) {
		String hexStr = Integer.toHexString(value & 0xFFFF);
		int len = hexStr.length();
		if (len == 1) {
			hexStr = "000" + hexStr;
		} else if (len == 2) {
			hexStr = "00" + hexStr;
		} else if (len == 3) {
			hexStr = "0" + hexStr;
		}
		return hexStr;
	}

	public static byte[] convertHexStringToByteArray(String str) {
		str = str.replaceAll(" ", "");
		double fLen = str.length();
		int nSize = (int) Math.ceil(fLen / 2);

		String strArray = null;
		byte[] bytes = new byte[nSize];

		// Keep the string oven length.
		if (nSize * 2 > fLen) {
			strArray = str + "0";
		} else {
			strArray = str;
		}
		for (int i = 0; i < nSize; i++) {
			int index = i * 2;
			char[] cArr = new char[] { strArray.charAt(index),
					strArray.charAt(index + 1) };
			String s = new String(cArr);
			try {
				int j = Integer.parseInt(s, 16);
				bytes[i] = (byte) j;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	public static void main(String[] args) {
		String t = "0A0FAAFF";
		byte[] tb = convertHexStringToByteArray(t);
		for (int i = 0; i < tb.length; i++) {
			System.out.println(tb[i]);
		}
		System.out.println(convertByteArrayToHexString(tb));
	}

	/*
	 * 本方法将返回buff中由位置pos开始，长度为len的字节数组拷贝
	 */
	public static List getArrayData(byte[] buff, int pos, int len) {
		List l = new ArrayList(2);
		byte[] rtn = new byte[len];
		System.arraycopy(buff, pos, rtn, 0, len);
		l.add(rtn);
		l.add(pos + len);
		return l;
	}

	public static List<Integer> getLength(byte[] buff, int pos, int len) {
		List<Integer> l = new ArrayList<Integer>(2);

		List lst = getArrayData(buff, pos, len);
		byte[] b = (byte[]) lst.get(0);
		pos = (Integer) lst.get(1);

		int length = 0;
		for (int i = 0; i < b.length; i++) {
			int base = b[i] & 0xff;
			for (int j = 1; j < b.length - i; j++) {
				base = base * 16 * 16;
			}
			length += base;
		}

		l.add(length);
		l.add(pos);
		return l;
	}

	/**
	 * 替换XML中特殊字符
	 *
	 * @param str
	 * @return
	 */
	public static String xmlStringReplace(String str) {
		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		str = str.replace("'", "&apos;");
		str = str.replace("\"", "&quot;");
		return str;
	}

	/**
	 * 还原XML中特殊字符
	 *
	 * @param str
	 * @return
	 */
	public static String xmlString(String str) {
		str = str.replace("&lt;", "<");
		str = str.replace("&gt;", ">");
		str = str.replace("&apos;", "'");
		str = str.replace("&quot;", "\"");
		str = str.replace("&quot;", "&");
		return str;
	}

	public static void logAndTriggerApiErr(String readerName, String errCode,
										   String exceptionMsg, LogType logType) {
		String errMsg = readerName + ":";
		String err = null;
		if (ErrorInfo.errMap.containsKey(errCode)) {
			err = ErrorInfo.errMap.get(errCode);
		}
		if (err != null) {
			errMsg += err;
		} else {
			errMsg += " ErrCode:" + errCode + " ";
		}
		if (!(exceptionMsg == null || exceptionMsg.equals(""))) {
			errMsg += exceptionMsg;
		}
		if (logType.equals(LogType.Fatal)) {
			Log.fatal(errMsg);
		}
		if (logType.equals(LogType.Warn)) {
			Log.warn(errMsg);
		}
		if (logType.equals(LogType.Error)) {
			if (err != null)
				Log.error(errMsg);
		}
		if (logType.equals(LogType.Info)) {
			Log.info(errMsg);
		}
		if (logType.equals(LogType.Debug)) {
			if (err != null)
				Log.debug(errMsg);
		}
	}

	public static int convertTagNumToQ(int tagNum) {
		int q = (int) Math.ceil(Math.log((double) tagNum)
				/ Math.log((double) 2));
		return q;
	}

	public static boolean isHexString(String str) {
		str = str.replace(" ", "");
		Matcher m = hexPattern.matcher(str);
		return m.matches();
	}

	public static byte[] getPwd(String pwdStr) {
		byte[] pwd = new byte[4];
		byte[] p = convertHexStringToByteArray(pwdStr.trim());
		if (p.length < 4) {
			System.arraycopy(p, 0, pwd, 4 - p.length, p.length);
		} else {
			pwd = p;
		}
		return pwd;
	}

	public enum LogType {
		Debug, Info, Error, Warn, Fatal
	}

	/**
	 * 获取读标签时间字符串
	 *
	 * @param readTime
	 * @return
	 */
	public static String readTimeToString(byte[] readTime) {
		String str = "";
		if (readTime == null)
			return "";
		if (readTime.length == 6) {
			str = "20" + Util.convertByteToHexWordString(readTime[0]) + "-"
					+ Util.convertByteToHexWordString(readTime[1]) + "-"
					+ Util.convertByteToHexWordString(readTime[2]) + " "
					+ Util.convertByteToHexWordString(readTime[3]) + ":"
					+ Util.convertByteToHexWordString(readTime[4]) + ":"
					+ Util.convertByteToHexWordString(readTime[5]) + "(BCD)";
		} else if (readTime.length == 8) {
			Calendar c = Calendar.getInstance();
			c.set(1970, 01, 01);
			int value = (readTime[0] << 24) + (readTime[1] << 16)
					+ (readTime[2] << 8) + readTime[3];
			c.set(Calendar.SECOND, value);
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			// DateTime dt = DateTime.Parse("1970-01-01").
			// AddSeconds((readTime[0] << 24) + (readTime[1] << 16) +
			// (readTime[2] << 8) + readTime[3]);
			str = format.format(c.getTime());// dt.ToString("yyyy-MM-dd
			// HH:mm:ss");
			long ms = ((long) ((readTime[4] << 24) + (readTime[5] << 16)
					+ (readTime[6] << 8) + (readTime[7])) / 1000);
			if (ms < 1000)
				str += "." + padLeft(((Long) ms).toString(), 3, '0') + "(UTC)";
			// else
			// str = Localization.GetMessageList("MSG_187");
		}
		return str;
	}

	public static String padLeft(String original, int expectLeng, char replace) {
		int length = original.length();
		if (expectLeng <= length) {
			return original;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < expectLeng - length; i++) {
				sb.append(replace);
			}
			sb.append(original);
			return sb.toString();
		}
	}
}
