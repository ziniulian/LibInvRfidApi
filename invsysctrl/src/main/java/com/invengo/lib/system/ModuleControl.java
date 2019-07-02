package com.invengo.lib.system;

import com.invengo.lib.diagnostics.InvengoLog;
import com.invengo.lib.system.device.DeviceManager;
import com.invengo.lib.system.device.DeviceConfigManager;
import com.invengo.lib.system.device.type.DeviceType;

/*
 * 1.9.2015091500 : enable2dScannerEx함수를 F/W 업데이트로 사용
 * 1.10.2015091700 : Gun Trigger 파워 GPIO추가
 * 1.11.2015100100 : getHwVersion함수 추가 H/W 버전에 따라 GPIO 분기 호출
 * 1.12.2015102700 : power2dBarcodeDevice 함수에서 f/w update pin이 제어되도록 추가.
 * 1.13.2015111100 : 로그 출력 방식 변경 [Log => ATLog]
 * 1.13.2015111400 : GPIO 테스트(Device Option 후 Keyboard Mapping이 흐트러지 문제 검증)
 * 1.13.2016010800 : 2D barcode power off 순서 변경.(3680엔진이 꺼질 때, 에이머가 번쩍이는 문제 때문)
 * 1.13.2016012500 : power1dBarcodeDevice 함수에서도 f/w update pin이 제어되도록 수정.
 * 1.13.2016012600 : AT870A 적용
 * 1.14.2016041800 : AT870A RFID 적용
 */

// ------------------------------------------------------------------------
// Module  Control ( Use Lib Only )
// ------------------------------------------------------------------------

public class ModuleControl {

	private static final String TAG = ModuleControl.class.getSimpleName();
	private static final String VERSION = "1.0";

	private static final String XCRF1003 = "XCRF";

	// 1D Barcode Device Power On/Off
	public static void power1dBarcodeDevice(boolean enabled) {

		DeviceType type = DeviceManager.getDeviceType();
		int platform = type.getCode();

		InvengoLog.i(TAG, "INFO. Type [%s:%d] Power Control 1D Barcode (%s)", type, platform, enabled);

		switch (type) {
			case AT911:
			case AT911_HILTI_US:
			case AT911_HILTI_EU:
				// 2015.09.08 f/w update로 사용함.
				enable2dScanner(false);
				if (enabled) {
					enableScanTrigger(enabled);
					enableScanAim(enabled);
					powerScan(enabled);
				} else {
					powerScan(enabled);
					enableScanTrigger(enabled);
					enableScanAim(enabled);
				}
				break;
			case AT911N:
			case XC2910://add by invengo at 2017.04.26
			case XC9910:
			case AT511:
			case AT911N_HILTI_US:
			case AT911N_HILTI_EU:
			case AT870A:
				// 2015.09.08 f/w update로 사용함.
				// enable2dScannerEx(false, platform);

				if (enabled) {
					enableScanTriggerEx(enabled, platform);
					enableScanAimEx(enabled, platform);
					powerScanEx(enabled, platform);
					enable2dScannerEx(false, platform); // 2016.01.25 for H/W test
				} else {
					powerScanEx(enabled, platform);
					enable2dScannerEx(true, platform); // 2016.01.25 for H/W test
					enableScanTriggerEx(enabled, platform);
					enableScanAimEx(enabled, platform);
				}
				break;
			case AT311:
			case AT312:
			case XCRF1003:
				powerExt99Device(enabled);
				break;
			default:
				break;
		}

	}

	// 2D Barocde Device Power On/Off
	public static void power2dBarcodeDevice(boolean enabled) {
		DeviceType type = DeviceManager.getDeviceType();
		int platform = type.getCode();

		InvengoLog.i(TAG, "INFO. Power Control 2D Barcode (%s)", enabled);

		switch (type) {
			case AT911:
			case AT911_HILTI_US:
			case AT911_HILTI_EU:
				enable2dScanner(true);
				if (enabled) {
					enableScanTrigger(enabled);
					enableScanAim(enabled);
					powerScan(enabled);
				} else {
					powerScan(enabled);
					enableScanTrigger(enabled);
					enableScanAim(enabled);
				}
				break;
			case AT911N:
			case XC2910://add by invengo at 2017.04.26
			case XC9910:
			case AT511:
			case AT911N_HILTI_US:
			case AT911N_HILTI_EU:
			case AT870A:
				// 2015.09.08 f/w update로 사용함.
				// enable2dScannerEx(true, platform);

				if (enabled) {
					powerScanEx(enabled, platform);
					enableScanTriggerEx(enabled, platform);
					enableScanAimEx(enabled, platform);
					enable2dScannerEx(false, platform); // 2015.10.21 for H/W test
				} else {

					powerScanEx(enabled, platform);
					enable2dScannerEx(true, platform); // 2015.10.21 for H/W test
					enableScanTriggerEx(enabled, platform);
					enableScanAimEx(enabled, platform);
				
				/*
				enableScanTriggerEx(enabled, platform);
				enableScanAimEx(enabled, platform);
				powerScanEx(enabled, platform);
				enable2dScannerEx(true, platform); // 2015.10.21 for H/W test
				*/
				}
				break;
			case AT311:
			case AT312:
			case XCRF1003:
				powerExt99Device(enabled);
				break;
			default:
				break;
		}
	}

	// ????? Firmware Update
	public static void enableBarcodeFirmwareUpdate(boolean enabled) {
		DeviceType type = DeviceManager.getDeviceType();
		int platform = type.getCode();

		InvengoLog.i(TAG, "INFO. Barcode firmware update (%s)", enabled);

		switch (type) {
			case AT911:
			case AT911_HILTI_US:
			case AT911_HILTI_EU:
				InvengoLog.e(TAG, "Not supported!");
				break;
			case AT911N:
			case XC2910://add by invengo at 2017.04.26
			case XC9910:
			case AT511:
			case AT911N_HILTI_US:
			case AT911N_HILTI_EU:
			case AT870A:
				// 2015.09.08 f/w update로 사용함.
				enable2dScannerEx(enabled, platform);
				break;
			case AT311:
			case AT312:
			case XCRF1003:
				InvengoLog.e(TAG, "Not supported!");
				break;
			default:
				break;
		}
	}

	// RFID Device Power On/Off ( For AT311 SDK )
	public static void powerRfidModule(boolean enabled) {

		DeviceType type = DeviceManager.getDeviceType();
		int platform = type.getCode();
		int moduleType = DeviceConfigManager.getInstance().getRfidType().getCode();

		InvengoLog.i(TAG, "INFO. powerRfidModule(%s) - {%s, %d, %d}", enabled, type, platform, moduleType);

		switch (type) {
			case AT911:
			case AT911_HILTI_US:
			case AT911_HILTI_EU:
				powerRfid(enabled);
				break;
			case AT911N_HILTI_US:
			case AT911N_HILTI_EU:
			case AT911N:
			case XC2910://add by invengo at 2017.04.26
			case XC9910:
			case AT511:
			case AT870A:
				powerRfidEx(enabled, platform, moduleType);
				break;
			case AT311:
			case AT312:
			case XCRF1003:
				powerExt99Device(enabled);
				break;
			default:
				// throw new ATNotSupportedModuleException(type);
				break;
		}
	}

	// RFID Device Power On/Off ( For AT911 SDK )
	public static void powerRfidDevice(boolean enabled) {

		DeviceType type = DeviceManager.getDeviceType();
		int platform = type.getCode();
		int moduleType = DeviceConfigManager.getInstance().getRfidType().getCode();

		InvengoLog.i(TAG, "INFO. powerRfidDevice(%s) - {%s, %d, %d}", enabled, type, platform, moduleType);

		switch (type) {
			case AT911:
			case AT911_HILTI_US:
			case AT911_HILTI_EU:
				powerRfid(enabled);
				break;
			case AT911N_HILTI_US:
			case AT911N_HILTI_EU:
			case AT911N:
			case XC2910://add by invengo at 2017.04.26
			case XC9910:
			case AT511:
			case AT870A:
				powerRfidEx(enabled, platform, moduleType);
				break;
			case AT311:
			case AT312:
			case XCRF1003:
				powerExt99Device(enabled);
				break;
			default:
				// throw new ATNotSupportedModuleException(type);
				break;
		}
	}

	// RFID Device Reset
	public static void resetRfidDevice() {

		DeviceType type = DeviceManager.getDeviceType();
		int platform = type.getCode();

		InvengoLog.i(TAG, "INFO. resetRfidDevice() - {%s, %d}", type, platform);

		switch (type) {
			case AT911:
			case AT911_HILTI_US:
			case AT911_HILTI_EU:
				resetRfid();
				break;
			case AT911N:
			case XC2910://add by invengo at 2017.04.26
			case XC9910:
			case AT511:
			case AT911N_HILTI_US:
			case AT911N_HILTI_EU:
			case AT870A:
				resetRfidEx(platform);
				break;
			case AT311:
			case AT312:
			case XCRF1003:
				break;
			default:
				break;
		}
	}

	// ------------------------------------------------------------------------
	// Java Native Interface
	// ------------------------------------------------------------------------
	// AT911
	private native static void resetRfid();

	private native static void powerRfid(boolean enabled);

	private native static void powerScan(boolean enabled);

	private native static void enable2dScanner(boolean enabled);

	private native static void enableScanTrigger(boolean enabled);

	private native static void enableScanAim(boolean enabled);

	// AT311
	private native static void powerExtDevice(boolean enabled);

	private native static void powerExt97Device(boolean enabled);

	private native static void powerExt99Device(boolean enabled);

	// 2015.05.01 yjcho
	// AT911N & AT511
	private native static void resetRfidEx(int platform);

	private native static void powerRfidEx(boolean enabled, int platform, int module);

	private native static void powerScanEx(boolean enabled, int platform);

	private native static void enable2dScannerEx(boolean enabled, int platform);

	private native static void enableScanTriggerEx(boolean enabled, int platform);

	private native static void enableScanAimEx(boolean enabled, int platform);

	public native static String getLibVersion();

	public native static int getHwVersion();

	static {
		System.loadLibrary("system_control");
	}

	// get SDK Version
	public static String getVersion() {
		return VERSION;
	}
}
