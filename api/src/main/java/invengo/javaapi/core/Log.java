package invengo.javaapi.core;

import invengo.javaapi.handle.ILogChangeDelegate;
import invengo.javaapi.handle.LogChangeEventArgs;

import java.util.ArrayList;
import java.util.List;

public class Log implements ILogChangeDelegate {

	public static List<ILogChangeDelegate> OnLogChanged = new ArrayList<ILogChangeDelegate>();

	public static void debug(String logStr) {
		LogManager.getInstance().write(logStr, "DEBUG");
	}

	public static void info(String logStr) {
		LogManager.getInstance().write(logStr, "INFO");
	}

	public static void warn(String logStr) {
		LogManager.getInstance().write(logStr, "WARN");
	}

	public static void error(String logStr) {
		LogManager.getInstance().write(logStr, "ERROR");
	}

	public static void fatal(String logStr) {
		LogManager.getInstance().write(logStr, "FATAL");
	}

	static void onLogChangedMethod(LogChangeEventArgs arg) {
		if (OnLogChanged != null) {
			for (int i = 0; i < OnLogChanged.size(); i++) {
				OnLogChanged.get(i).logChangeDelegate(arg);
			}
		}
	}

	public void logChangeDelegate(LogChangeEventArgs arg) {
		onLogChangedMethod(arg);
	}

}