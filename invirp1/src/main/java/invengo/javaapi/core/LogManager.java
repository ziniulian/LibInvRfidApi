package invengo.javaapi.core;

import invengo.javaapi.handle.LogChangeEventArgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class LogManager {

	private String logName = "DebugLog.log";
	private String bakName = "DebugLog.bak";
	private PrintWriter printWriter = null;
	private File file = null;

	private static final LogManager instance = new LogManager();

	public static LogManager getInstance() {
		return instance;
	}

	private LogManager() {
		reset();
	}

	private void reset() {
		File dFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
//		File dFile = Environment.getExternalStorageDirectory();
		if (!dFile.exists()) {
			try {
				dFile.mkdir();
				String filePath = dFile.getAbsolutePath() + File.separator + logName;
				file = new File(filePath);
				if(!file.exists()){
					file.createNewFile();
				}
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
				osw.write("Invengo RFID Log \r\n");
				osw.flush();
				osw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String filePath = dFile.getAbsolutePath() + File.separator + logName;
		file = new File(filePath);
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			printWriter = new PrintWriter(new FileOutputStream(file, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void write(String msg, String logFlag) {

		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(new Date());
		String threadId = Thread.currentThread().getId() + "";
		printWriter.write(time + " [" + threadId + "] " + logFlag + " - " + msg + "\r\n");
		printWriter.flush();
		printWriter.close();
		Log.onLogChangedMethod(new LogChangeEventArgs(msg, time, threadId, logFlag));
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
