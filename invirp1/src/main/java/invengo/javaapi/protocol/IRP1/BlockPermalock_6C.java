package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 块永久锁指令
 */

public class BlockPermalock_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            密码(4字节)
	 * @param memoryBank
	 *            操作Bank
	 * @param readLock
	 *            操作类型(ReadLock):0，查询标签的块永久锁状态（目前不支持）;1，对标签的进行块永久锁操作
	 * @param ptr
	 *            起始块地址(BlockPtr)：每16个块（Block）为一个单元，如0x00表示从block
	 *            0开始，0x01表示从block 16开始。
	 * @param blockRange
	 *            块永久锁范围BlockRange：指示块永久锁操作的范围为从起始块地址（BlockPtr）到（16*
	 *            BlockRange-1），最小值为1。
	 * @param mask
	 *            掩码（Mask）2*BlockRange字节：每一位对应一个block以指示从对应的block是否进行块永久锁操作，1为执行永久锁，0为保持原有锁状态，BlockPtr对应Mask最高位，（16*
	 *            BlockRange-1）对应Mask的最低位。
	 */
	public BlockPermalock_6C(byte antenna, byte[] accessPwd, byte memoryBank,
							 byte readLock, int ptr, byte blockRange, byte readLockbits,
							 byte[] mask) {
		if (accessPwd == null || mask == null) {
			throw new IllegalArgumentException();
		}
		byte[] ptrArray = EVB.convertToEvb(ptr);
		super.msgBody = new byte[9 + 2 * blockRange + ptrArray.length];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
		super.msgBody[5] = memoryBank;
		super.msgBody[6] = readLock;
		System.arraycopy(ptrArray, 0, msgBody, 7, ptrArray.length);
		super.msgBody[7 + ptrArray.length] = blockRange;
		super.msgBody[8 + ptrArray.length] = readLockbits;
		System.arraycopy(mask, 0, msgBody, 9 + ptrArray.length, 2 * blockRange);
	}

	public BlockPermalock_6C(byte antenna, byte[] accessPwd, byte memoryBank,
							 byte readLock, int ptr, byte blockRange, byte readLockbits,
							 byte[] mask, byte[] tagID, MemoryBank tagIDType) {
		this(antenna, accessPwd, memoryBank, readLock, ptr, blockRange,
				readLockbits, mask);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public BlockPermalock_6C() {
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}