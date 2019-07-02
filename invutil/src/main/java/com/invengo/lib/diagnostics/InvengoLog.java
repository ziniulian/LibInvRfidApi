package com.invengo.lib.diagnostics;

import android.os.Environment;

import com.invengo.lib.util.SysUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public final class InvengoLog {

	private static String TAG = InvengoLog.class.getSimpleName();

	private static final boolean DEBUG = true;

	private static final String PREFIX_DEBUG = "D";
	private static final String PREFIX_VERBOSE = "V";
	private static final String PREFIX_INFO = "I";
	private static final String PREFIX_WARN = "W";
	private static final String PREFIX_ERROR = "E";

	private static final int MAX_LAST_LOG_COUNT = 3;

	private static Queue<QueueItem> mQueue = null;
	private static Thread mLogThread = null;
	private static boolean mIsAliveLogThread = false;

	private static String mLogPath = "";
	private static String mLogName = "";

	// ------------------------------------------------------------------------
	// Public Logging Method
	// ------------------------------------------------------------------------

	public static void v(String tag, String msg) {
		if (!DEBUG)
			return;

		if (mQueue != null) {
			synchronized (mQueue) {
				mQueue.offer(new QueueItem(System.currentTimeMillis(), PREFIX_VERBOSE, tag, msg));
			}
		}

		android.util.Log.v(tag, msg);
	}

	public static void v(String tag, String format, Object... args) {
		v(tag, String.format(Locale.US, format, args));
	}

	public static void v(String tag, Throwable tr, String format, Object... args) {
		v(tag, String.format(Locale.US, format, args) + "\r\n" + getStackTraceString(tr));
	}

	public static void d(String tag, String msg) {
		if (!DEBUG)
			return;

		if (mQueue != null) {
			synchronized (mQueue) {
				mQueue.offer(new QueueItem(System.currentTimeMillis(), PREFIX_DEBUG, tag, msg));
			}
		}

		android.util.Log.d(tag, msg);
	}

	public static void d(String tag, String format, Object... args) {
		d(tag, String.format(Locale.US, format, args));
	}

	public static void d(String tag, Throwable tr, String format, Object... args) {
		d(tag, String.format(Locale.US, format, args) + "\r\n" + getStackTraceString(tr));
	}

	public static void i(String tag, String msg) {
		if (!DEBUG)
			return;

		if (mQueue != null) {
			synchronized (mQueue) {
				mQueue.offer(new QueueItem(System.currentTimeMillis(), PREFIX_INFO, tag, msg));
			}
		}

		android.util.Log.i(tag, msg);
	}

	public static void i(String tag, String format, Object... args) {
		i(tag, String.format(Locale.US, format, args));
	}

	public static void i(String tag, Throwable tr, String format, Object... args) {
		i(tag, String.format(Locale.US, format, args) + "\r\n" + getStackTraceString(tr));
	}

	public static void w(String tag, String msg) {
		if (!DEBUG)
			return;

		if (mQueue != null) {
			synchronized (mQueue) {
				mQueue.offer(new QueueItem(System.currentTimeMillis(), PREFIX_WARN, tag, msg));
			}
		}

		android.util.Log.w(tag, msg);
	}

	public static void w(String tag, String format, Object... args) {
		w(tag, String.format(Locale.US, format, args));
	}

	public static void w(String tag, Throwable tr, String format, Object... args) {
		w(tag, String.format(Locale.US, format, args) + "\r\n" + getStackTraceString(tr));
	}

	public static void e(String tag, String msg) {
		if (!DEBUG)
			return;

		if (mQueue != null) {
			synchronized (mQueue) {
				mQueue.offer(new QueueItem(System.currentTimeMillis(), PREFIX_ERROR, tag, msg));
			}
		}

		android.util.Log.e(tag, msg);
	}

	public static void e(String tag, String format, Object... args) {
		e(tag, String.format(Locale.US, format, args));
	}

	public static void e(String tag, Throwable tr, String format, Object... args) {
		e(tag, String.format(Locale.US, format, args) + "\r\n" + getStackTraceString(tr));
	}

	// ------------------------------------------------------------------------
	// Output Stack Trace Message
	// ------------------------------------------------------------------------

	private static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	private static void INFO(String tag, String msg) {
		android.util.Log.i(tag, msg);
	}

	private static void INFO(String tag, String format, Object... args) {
		android.util.Log.i(tag, String.format(Locale.US, format, args));
	}

	private static void ERROR(String tag, Throwable tr, String msg) {
		android.util.Log.e(tag, msg, tr);
	}

	private static void ERROR(String tag, Throwable tr, String format, Object... args) {
		android.util.Log.e(tag, String.format(Locale.US, format, args), tr);
	}

	// ------------------------------------------------------------------------
	// Start/Stop Backgound Logging Service
	// ------------------------------------------------------------------------

	public static void startUp(String path, String name) {
		mLogPath = path;
		mLogName = name;
		mQueue = new LinkedList<QueueItem>();
		mIsAliveLogThread = false;
		mLogThread = new Thread(mLogThreadPoc);
		mLogThread.start();

		INFO(TAG, "INFO. startUp([%s], [%s])", path, name);
	}

	public static void shutdown() {
		if (mLogThread == null && mQueue == null)
			return;
		if (mLogThread != null) {
			if (mLogThread.isAlive()) {
				mIsAliveLogThread = false;
				try {
					mLogThread.join();
				} catch (InterruptedException e) {
				}
			}
			mLogThread = null;
		}
		mQueue = null;

		INFO(TAG, "INFO. shutdown()");
	}

	// ------------------------------------------------------------------------
	// Internal Logging Thread Procedure
	// ------------------------------------------------------------------------

	private static Runnable mLogThreadPoc = new Runnable() {

		private BufferedWriter mWriter;

		@Override
		public void run() {

			mIsAliveLogThread = true;

			INFO(TAG, "INFO. Begin log thread");

			removeAllFiles();

			while (mIsAliveLogThread) {

				if (mQueue.size() > 0) {
					synchronized (mQueue) {
						writeLog(mQueue.poll());
					}
				} else {
					try {
						if( mWriter != null)
							mWriter.flush();
					} catch (IOException e) {
						ERROR(TAG, e, "ERROR. Failed to close file writer");
					}
					SysUtil.sleep(100);
				}
			}

			while (mQueue.size() > 0) {
				synchronized (mQueue) {
					writeLog(mQueue.poll());
				}
			}

			try {
				if( mWriter != null) {
					mWriter.flush();
					mWriter.close();
				}
			} catch (IOException e) {
				ERROR(TAG, e, "ERROR. Failed to close file writer");
			}

			INFO(TAG, "INFO. End log thread");
		}

		private synchronized void removeAllFiles() {
			String path = getFilePath();
			File dir = new File(path);

			// Get Log File List...
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".log");
				}

			});
			if (files == null)
				return;
			if (files.length < MAX_LAST_LOG_COUNT)
				return;

			// Sort Files
			Arrays.sort(files, new Comparator<File>() {

				@Override
				public int compare(File lhs, File rhs) {
					String s1 = lhs.lastModified() + "";
					String s2 = rhs.lastModified() + "";
					return s1.compareTo(s2);
				}

			});

			int count = files.length - MAX_LAST_LOG_COUNT;
			String fileName;
			for (int i = 0; i < count; i++) {
				fileName = files[i].toString();
				files[i].delete();
				INFO(TAG, "DEBUG. Remove File : %s", fileName);
			}
			INFO(TAG, "INFO. removeAllFiles()");
		}

		private synchronized BufferedWriter getLogFile() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
			String logPath = getFilePath();
			String filePath = String.format("%s%s_%s.log", logPath, mLogName,
					sdf.format(new Date(System.currentTimeMillis())));

			File file = new File(filePath);
			BufferedWriter writer = null;

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					ERROR(TAG, e, "ERROR. getLogFile() - Failed to create new file [%s]", filePath);
					return null;
				}
			}

			try {
				writer = new BufferedWriter(new FileWriter(filePath, true));
			} catch (IOException e) {
				ERROR(TAG, e, "ERROR. getLogFile() - Failed to create file writer [%s]", filePath);
				return null;
			}

			INFO(TAG, "INFO. getLogFile() - [%s]", filePath);
			return writer;
		}

		private synchronized void writeLog(QueueItem item) {

			if (mWriter == null)
				mWriter = getLogFile();

			try {
				mWriter.write(item.toString() + "\r\n");
			} catch (IOException e) {
				mWriter = getLogFile();
				if (mWriter != null) {
					try {
						mWriter.write(item.toString() + "\r\n");
					} catch (IOException e1) {
						ERROR(TAG, e, "ERROR. writeLog([%s]) - Failed to write log", item.toString());
						return;
					}
				}
				return;
			}
		}

		private synchronized String getFilePath() {
			String sdcard = Environment.getExternalStorageState();
			File file = null;

			if (!sdcard.equals(Environment.MEDIA_MOUNTED))
				file = Environment.getRootDirectory();
			else
				file = Environment.getExternalStorageDirectory();

			if (!mLogPath.startsWith("/"))
				mLogPath = "/" + mLogPath;
			if (!mLogPath.endsWith("/"))
				mLogPath = mLogPath + "/";
			String dir = file.getAbsolutePath() + mLogPath;

			file = new File(dir);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					InvengoLog.d(TAG, "DEBUG. getFilePath() - Failed to make directory [%s]", file.getAbsoluteFile());
				}
				if (!file.setExecutable(true)) {
					InvengoLog.d(TAG, "DEBUG. getFilePath() - Failed to set executeable [%s]", file.getAbsoluteFile());
				}
				if (!file.setReadable(true)) {
					InvengoLog.d(TAG, "DEBUG. getFilePath() - Failed to set readable [%s]", file.getAbsoluteFile());
				}
				if (!file.setWritable(true)) {
					InvengoLog.d(TAG, "DEBUG. getFilePath() - Failed to set writable [%s]", file.getAbsoluteFile());
				}
			}

			INFO(TAG, "INFO. getFilePath() - [%s]", dir);
			return dir;
		}
	};

	// ------------------------------------------------------------------------
	// Internal QueueItem Class
	// ------------------------------------------------------------------------

	private static class QueueItem {
		private long mTime;
		private String mType;
		private String mTag;
		private String mMsg;

		private QueueItem(long time, String type, String tag, String msg) {
			mTime = time;
			mType = type;
			mTag = tag;
			mMsg = msg;
		}

		private String getTime() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
			return sdf.format(new Date(mTime));
		}

		@Override
		public String toString() {
			return String.format("%s: %s/%s: %s", getTime(), mType, mTag, mMsg);
		}
	}
}
