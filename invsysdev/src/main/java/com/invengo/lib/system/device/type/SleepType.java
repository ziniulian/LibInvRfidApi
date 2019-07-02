package com.invengo.lib.system.device.type;

public enum SleepType {
	None(0, "None"),
	WithSleep(1, "With Sleep"),
	WithoutSleep(2, "Without Sleep");
	
	private int code;
	private String name;
	
	private SleepType(int code, String name) {
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
	
	public static SleepType valueOf(int code) {
		for (SleepType item : values()) {
			if (item.getCode() == code) {
				return item;
			}
		}
		return SleepType.None;
	}
}
