package com.invengo.lib.system.device.type;

public enum NfcModuleStateType {
	None(0, "None"),
	Disable(1, "Disable"),
	Enable(2, "Enable");
	
	private int code;
	private String name;
	
	private NfcModuleStateType(int code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public int getCode() {
		return this.code;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	public static NfcModuleStateType valueOf(int code) {
		for (NfcModuleStateType item : values()) {
			if (item.getCode() == code) {
				return item;
			}
		}
		return NfcModuleStateType.None;
	}
}
