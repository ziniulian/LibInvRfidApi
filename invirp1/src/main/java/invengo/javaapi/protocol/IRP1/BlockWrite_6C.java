package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;
import invengo.javaapi.protocol.IRP1.EVB;

/**
 * 块写Bank数据指令
 */

public class BlockWrite_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            标签访问密码
	 * @param memoryBank
	 *            操作块
	 * @param ptr
	 *            标签数据区首地址（EVB格式）
	 * @param blockSize
	 *            块长度，单位为字
	 * @param data
	 *            写数据区数据
	 */
	public BlockWrite_6C(byte antenna, byte[] accessPwd, MemoryBank memoryBank,
						 int ptr, byte blockSize, byte[] data) {
		if (accessPwd==null||data==null) {
			throw new IllegalArgumentException();
		}
		byte[] ptrArray = EVB.convertToEvb(ptr);
		super.msgBody = new byte[7 + data.length + ptrArray.length];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
		super.msgBody[5] = memoryBank.getValue();
		System.arraycopy(ptrArray, 0, msgBody, 6, ptrArray.length);
		super.msgBody[6 + ptrArray.length] = (byte) (blockSize);
		System.arraycopy(data, 0, msgBody, 7 + ptrArray.length, data.length);
	}

	public BlockWrite_6C(byte antenna, byte[] accessPwd, MemoryBank memoryBank,
						 int ptr, byte blockSize, byte[] data, byte[] tagID,
						 MemoryBank tagIDType) {
		this(antenna, accessPwd, memoryBank, ptr, blockSize, data);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public BlockWrite_6C() {
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}
}
