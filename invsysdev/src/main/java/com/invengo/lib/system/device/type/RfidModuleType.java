package com.invengo.lib.system.device.type;

public enum RfidModuleType {
	None(0, "None"),
	I900MA(1, "I900MA"),
	Invengo6EM_1(2, "Invengo6EM-1"),//modify by invengo at 2017.02.27
	Invengo9200P_1(3, "Invengo9200P-1"),//modify by invengo at 2017.02.27
	InvengoX00S_1(4, "InvengoX00S-1");//modify by invengo at 2017.02.27
	
	private int code;
	private String name;
	
	private RfidModuleType(int code, String name) {
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
	
	public static RfidModuleType valueOf(int code) {
		for (RfidModuleType item : values()) {
			if (item.getCode() == code) {
				return item;
			}
		}
		return RfidModuleType.None;
	}
}
