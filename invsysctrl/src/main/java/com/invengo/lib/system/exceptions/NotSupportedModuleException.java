package com.invengo.lib.system.exceptions;

import java.util.Locale;

import com.invengo.lib.system.device.type.DeviceType;

public class NotSupportedModuleException extends Exception {

	private static final long serialVersionUID = 2000L;
	private final DeviceType mDevType;
	
	public NotSupportedModuleException(DeviceType type) {
		super(String.format(Locale.US, "Not supported modeul at [%s]", type.toString()));
		mDevType = type;
	}
	
	public DeviceType getDeviceType() {
		return mDevType;
	}
}
