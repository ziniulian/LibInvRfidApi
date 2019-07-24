package invengo.javaapi.protocol.IRP1;

import com.invengo.lib.diagnostics.InvengoLog;
import com.invengo.lib.system.device.DeviceConfigManager;
import com.invengo.lib.system.device.DeviceManager;
import com.invengo.lib.system.device.type.DeviceType;
import com.invengo.lib.system.device.type.RfidModuleType;

/**
 * 用于一体式手持机获取读写器对象
 */
public class IntegrateReaderManager {

	private static final String TAG = IntegrateReaderManager.class.getSimpleName();
	private static final int BAUDRATE = 115200;
	private static final String VERSION = "2.16.2017022409";

	private static Reader invengoReader = null;
	private static int smRefCount = 0;

	@SuppressWarnings("unused")
	private static final boolean DEBUG = true;

	// Get Reader Instance
	public static Reader getInstance() {
		if (invengoReader == null) {

			DeviceConfigManager manager = DeviceConfigManager.getInstance();
			RfidModuleType type = manager.getRfidType();

			InvengoLog.i(TAG, "INFO. getInstance() - {%s}", type);

			//根据RFID模块的型号来获取读写器实例
			switch (type) {
				case I900MA:
					InvengoLog.e(TAG, "ERROR. getInstance() - Not supported I900MA reader");
					return null;
				case Invengo6EM_1:
					InvengoLog.e(TAG, "ERROR. getInstance() - Not supported AT6EM reader");
					return null;
				case Invengo9200P_1:
					InvengoLog.e(TAG, "ERROR. getInstance() - Not supported AT9200 reader");
					return null;
				case InvengoX00S_1:
					invengoReader = new Reader("Reader1", "RS232", String.format("%s,%s", getPortName(), String.valueOf(BAUDRATE)));
					smRefCount++;
					return invengoReader;
				default:
					break;
			}

			// recheck system RFID module
			return checkAutoModule();
		}

		smRefCount++;
		InvengoLog.i(TAG, "INFO. getInstance() - {%d}", smRefCount);
		return invengoReader;

	}

	public static Reader checkAutoModule() {

		InvengoLog.i(TAG, "+++INFO. checkAutoModule()");

		Reader reader = null;
		DeviceConfigManager manager = DeviceConfigManager.getInstance();

		for (RfidModuleType type : RfidModuleType.values()) {
			if ((reader = checkModule(type)) != null) {
				manager.setRfidType(type);
				manager.save();

				invengoReader = reader;
				smRefCount++;

				InvengoLog.i(TAG, "---INFO. checkAutoModule() - {%s}", type);
				return invengoReader;
			}
		}

		InvengoLog.e(TAG, "ERROR. checkAutoModule() - Not found UHF module");

		return null;
	}

	public static Reader checkModule(RfidModuleType type) {
		InvengoLog.i(TAG, "+++INFO. checkModule(%s)", type);

		Reader reader = null;

		switch (type) {
			case I900MA:
				InvengoLog.e(TAG, "ERROR. checkModule(%s) - Not supported I900MA reader", type); //Modified by Lin
				return null;
			case Invengo6EM_1:
				InvengoLog.e(TAG, "ERROR. checkModule(%s) - Not supported AT6EM reader", type);
				return null;
			case Invengo9200P_1:
				InvengoLog.e(TAG, "ERROR. checkModule(%s) - Not supported AT9200 reader", type);
				return null;
			case InvengoX00S_1:
				reader = new Reader("Reader1", "RS232", String.format("%s,%s", getPortName(), String.valueOf(BAUDRATE)));
				break;
			default:
				InvengoLog.e(TAG, "ERROR. checkModule(%s) - Unknown module type", type);
				return null;
		}

		if (!reader.check()) {
			InvengoLog.e(TAG, "ERROR. checkModule(%s) - Failed to check module", type);
			return null;
		}

		InvengoLog.i(TAG, "---INFO. checkModule(%s)", type);
		return reader;
	}

	//	protected void wakeUp() {
	//		InvengoLog.i(TAG, "+++INFO. wakeUp()");
	//		if (invengoReader == null) {
	//			InvengoLog.e(TAG, "ERROR. wakeUp() - Failed to invalidate reader instance");
	//			return;
	//		}
	//		InvengoLog.d(TAG, "DEBUG. wakeUp() - do wakeUp");
	//		invengoReader.wakeUp();
	//	}

	//	protected void sleep() {
	//		InvengoLog.i(TAG, "+++INFO. sleep()");
	//		if (invengoReader == null) {
	//			InvengoLog.e(TAG, "ERROR. sleep() - Failed to invalidate reader instance");
	//			return;
	//		}
	//		InvengoLog.d(TAG, "DEBUG. sleep() - do sleep");
	//		invengoReader.sleep();
	//	}

	// get SDK Version
	public static String getVersion() {
		return VERSION;
	}

	public static String getPortName() {
		String portName = "";
		DeviceType type = DeviceManager.getDeviceType();

		switch (type) {
			case AT911:
				portName = "/dev/ttyS3";
				break;
			case AT911N:
			case XC2910:
			case XC9910:
				portName = "/dev/ttyS4";
				break;
			case XCRF1003:
			case AT312:
			case XC2900:
			case XC2903:
			case XC2910_V3:
				portName = "/dev/ttyMT1";
				break;
			case G6818:
				portName = "/dev/ttySAC3";
				break;
			default:
				InvengoLog.e(TAG, "ERROR. getPortName() - Not supported device [%s]", type);
				return "";
		}
		InvengoLog.i(TAG, "INFO. getPortName() - [%s]", portName);
		return portName;
	}

}
