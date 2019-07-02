package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;
import invengo.javaapi.protocol.IRP1.EVB;

/**
 * 块擦Bank数据指令
 */

public class BlockErase_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            标签访问密码
	 * @param memoryBank
	 *            操作块
	 * @param ptr
	 *            擦除标签数据区首地址（EVB格式）
	 * @param blockSize
	 *            块长度，单位为字
	 * @param blockCount
	 *            擦除数据块的数量
	 */
	public BlockErase_6C(byte antenna, byte[] accessPwd, MemoryBank memoryBank,
						 int ptr, byte blockSize, byte blockCount) {
		byte[] ptrArray = EVB.convertToEvb(ptr);
		super.msgBody = new byte[8 + ptrArray.length];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
		super.msgBody[5] = memoryBank.getValue();
		System.arraycopy(ptrArray, 0, msgBody, 6, ptrArray.length);
		super.msgBody[6 + ptrArray.length] = blockSize;
		super.msgBody[7 + ptrArray.length] = blockCount;
	}

	public BlockErase_6C(byte antenna, byte[] accessPwd, MemoryBank memoryBank,
						 int ptr, byte blockSize, byte blockCount, byte[] tagID,
						 MemoryBank tagIDType) {
		this(antenna, accessPwd, memoryBank, ptr, blockSize, blockCount);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public BlockErase_6C() {
	}


	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}
