package invengo.javaapi.core;

import invengo.javaapi.handle.LogChangeEventArgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

	private String logName = APIPath.folderName + File.separator + "Debug.log";
	private String bakName = APIPath.folderName + File.separator + "Debug.bak";
	private PrintWriter printWriter = null;
	private File file = null;

	private static final LogManager instance = new LogManager();

	public static LogManager getInstance() {
		return instance;
	}

	public LogManager() {
		reset();
	}

	private void reset() {
		if (!new File(logName).exists()) {
			try {
				OutputStreamWriter osw = new OutputStreamWriter(
						new FileOutputStream(logName), "utf-8");
				osw.write("Invengo RFID Log \r\n");
				osw.flush();
				osw.close();
			} catch (Exception e) {
			}
		}
		file = new File(logName);
		try {
			printWriter = new PrintWriter(new FileOutputStream(file, true));
		} catch (Exception e) {
		}
	}

	public void write(String msg, String logFlag) {

		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS")
				.format(new Date());
		String threadId = Thread.currentThread().getId() + "";
		printWriter.write(time + " [" + threadId + "] " + logFlag + " - " + msg
				+ "\r\n");
		printWriter.flush();
		printWriter.close();
		Log.onLogChangedMethod(new LogChangeEventArgs(msg, time, threadId,
				logFlag));
		if (file.length() > 1024 * 1024 * 2) {
			File bakFile = new File(bakName);
			if (bakFile.exists()) {
				bakFile.delete();
			}
			file.renameTo(new File(bakName));
			reset();
		}
	}
}
