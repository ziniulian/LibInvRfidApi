package invengo.javaapi.protocol.IRP1;

public class EVB {

	public static byte[] convertToEvb(int value) {
		String str = convertString(String.valueOf(value), 10, 2);
		if (str.length() % 7 > 0) {
			int index = str.length() + 7 - str.length() % 7;
			int max = index - str.length();
			for (int i = 0; i < max; i++) {
				str = "0" + str;
			}
		}
		byte[] evb = new byte[str.length() / 7];
		if (evb.length > 1) {
			for (int i = 0; i < evb.length; i++) {
				if (i == evb.length - 1) {
					evb[i] = (byte) Integer.parseInt("0"
							+ str.substring(7 * i, 7 * i + 7), 2);
				} else {
					evb[i] = (byte) Integer.parseInt("1"
							+ str.substring(7 * i, 7 * i + 7), 2);
				}
			}
		} else if (evb.length > 0) {
			evb[0] = (byte) Integer.parseInt("0" + str, 2);
		}
		return evb;
	}

	/**
	 * 2/8/10/16进制相互转换，value只能为正整数
	 *
	 * @param value
	 *            要转换的数字
	 * @param fromBase
	 *            源进制
	 * @param toBase
	 *            转换的进制
	 * @return 转换后的字符串
	 */
	private static String convertString(String value, int fromBase, int toBase) {
		int intValue = Integer.parseInt(value, fromBase);
		return Integer.toString(intValue, toBase);
	}

}
