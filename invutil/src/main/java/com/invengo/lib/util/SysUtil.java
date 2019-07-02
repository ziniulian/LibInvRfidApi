package com.invengo.lib.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.invengo.lib.diagnostics.InvengoLog;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.PowerManager;

public class SysUtil {

	private static final String TAG = SysUtil.class.getSimpleName();

	private static PowerManager.WakeLock smWakeLock = null;

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			InvengoLog.e(TAG, e, "ERROR. sleep(%d) - Failed to thread sleep", time);
		}
	}

	// Get Curent Date Time
	public static String getCurrentDateTime() {
		return getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
	}

	public static String getCurrentDateTime(String format) {
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		return sdf.format(date);
	}

	@SuppressWarnings("deprecation")
	public static void wakeLock(Context context, String name) {

		if (smWakeLock != null) {
			InvengoLog.e(TAG, "ERROR. wakeLock([%s]) - Already exist wake lock", name);
			return;
		}

		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		smWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, name);
		smWakeLock.acquire();

		InvengoLog.i(TAG, "INFO. wakeLock([%s])", name);
	}

	public static void wakeUnlock() {
		if (smWakeLock == null)
			return;

		smWakeLock.release();
		smWakeLock = null;

		InvengoLog.i(TAG, "INFO. wakeUnlock()");
	}

	public static String getVersion(Context context) {
		String packageName = context.getPackageName();
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_META_DATA).versionName;
		} catch (NameNotFoundException e) {
			InvengoLog.e(TAG, e, "ERROR. getVersion() - Failed to get package version");
			versionName = "";
		}
		InvengoLog.i(TAG, "INFO. getVersion() - {%s}", versionName);
		return versionName;
	}

	public static String getAppName(Context context) {
		String packageName = context.getPackageName();
		String appName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			appName = (String) packageManager.getApplicationLabel(
					packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES));
		} catch (NameNotFoundException e) {
			InvengoLog.e(TAG, e, "ERROR. getAppName() - Failed to get application name");
			appName = "";
		}
		InvengoLog.i(TAG, "INFO. getAppName() - {%s}", appName);
		return appName;
	}
}
