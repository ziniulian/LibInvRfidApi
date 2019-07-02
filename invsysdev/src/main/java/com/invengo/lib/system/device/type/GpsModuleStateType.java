package com.invengo.lib.system.device.type;

public enum GpsModuleStateType {
	None(0, "None"),
	Disable(1, "Disable"),
	Enable(2, "Enable");
	
	private int code;
	private String name;
	
	private GpsModuleStateType(int code, String name) {
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
	
	public static GpsModuleStateType valueOf(int code) {
		for (GpsModuleStateType item : values()) {
			if (item.getCode() == code) {
				return item;
			}
		}
		return GpsModuleStateType.None;
	}
}
