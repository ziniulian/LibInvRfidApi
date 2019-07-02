package invengo.javaapi.core;

public enum GBMemoryBank {

	//标签信息区
	GBTidMemory((byte) 0x00),
	//编码区
	GBEPCMemory((byte) 0x10),
	//安全区
	GBReservedMemory((byte) 0x20),
	// 用户子区1...依此类推
	GBUser1Memory((byte) 0x30),

	GBUser2Memory((byte) 0x31),

	GBUser3Memory((byte) 0x32),

	GBUser4Memory((byte) 0x33),

	GBUser5Memory((byte) 0x34),

	GBUser6Memory((byte) 0x35),

	GBUser7Memory((byte) 0x36),

	GBUser8Memory((byte) 0x37),

	GBUser9Memory((byte) 0x38),

	GBUser10Memory((byte) 0x39),

	GBUser11Memory((byte) 0x3A),

	GBUser12Memory((byte) 0x3B),

	GBUser13Memory((byte) 0x3C),

	GBUser14Memory((byte) 0x3D),

	GBUser15Memory((byte) 0x3E),

	GBUser16Memory((byte) 0x3F);

	private byte value;

	private GBMemoryBank(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

}
