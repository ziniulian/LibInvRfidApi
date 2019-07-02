package invengo.javaapi.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class APIPath {

	private static String getPath() {
		String string = null;
		try {
			string = URLDecoder.decode(new APIPath().getClass().getResource("")
					.getPath(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] strings = string.split("/");
		if (strings[1].indexOf(":") == -1) {
			strings[1] = File.separator + strings[1];
		}
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 1; i < strings.length - 4; i++) {
			sBuffer.append(strings[i]);
			sBuffer.append(File.separator);
		}
		String path = sBuffer.toString();
		if (path.contains("file:")) {
			path = path.substring(5);
		}
		return path;
	}

	public final static String folderName = getPath();

}
