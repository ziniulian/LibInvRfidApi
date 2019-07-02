package invengo.javaapi.core;

public enum MemoryBank {

	// 保留区
	ReservedMemory((byte) 0x00),
	// EPC数据区
	EPCMemory((byte) 0x01),
	// TID数据区
	TIDMemory((byte) 0x02),
	// 用户数据区
	UserMemory((byte) 0x03);

	private byte value;

	private MemoryBank(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

}
