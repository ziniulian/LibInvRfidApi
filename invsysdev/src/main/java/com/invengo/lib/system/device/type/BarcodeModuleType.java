package com.invengo.lib.system.device.type;

public enum BarcodeModuleType {
	None(0, "None"),
	Invengo1D955M_1(1, "Invengo1D955M-1"),//modify by invengo at 2017.02.27
	Invengo2D5X80I_1(2, "Invengo2D5X80I-1"),//modify by invengo at 2017.02.27
	Invengo2D4710M_1(3, "Invengo2D4710M-1"),//modify by invengo at 2017.02.27
	Invengo2DMDI3x00(4, "Invengo2DMDI-3x00");//modify by invengo at 2017.02.27
	
	private int code;
	private String name;
	
	private BarcodeModuleType(int code, String name) {
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
	
	public static BarcodeModuleType valueOf(int code) {
		for (BarcodeModuleType item : values()) {
			if (item.getCode() == code) {
				return item;
			}
		}
		return BarcodeModuleType.None;
	}
}
