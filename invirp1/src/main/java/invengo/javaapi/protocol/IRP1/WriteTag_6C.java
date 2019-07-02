package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 通用写标签指令
 */

public class WriteTag_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            密码(4字节)
	 * @param memoryBank
	 *            操作Bank
	 * @param ptr
	 *            首地址
	 * @param length
	 *            写入数据长度
	 * @param data
	 *            写入数据
	 */
	public WriteTag_6C(byte antenna, byte[] accessPwd, MemoryBank memoryBank,
					   int ptr, byte length, byte[] data) {
		byte[] ptrArray = EVB.convertToEvb(ptr);
		super.msgBody = new byte[7 + data.length + ptrArray.length];
		int p = 0;

		super.msgBody[0] = antenna;
		p += 1;

		System.arraycopy(accessPwd, 0, msgBody, p, accessPwd.length);
		p += accessPwd.length;

		super.msgBody[p] = memoryBank.getValue();
		p += 1;

		System.arraycopy(ptrArray, 0, msgBody, p, ptrArray.length);
		p += ptrArray.length;

		super.msgBody[p] = (byte) (length);
		p += 1;

		System.arraycopy(data, 0, msgBody, p, data.length);
	}

	public WriteTag_6C(byte antenna, byte[] accessPwd, MemoryBank memoryBank,
					   byte ptr, byte length, byte[] data, byte[] tagID,
					   MemoryBank tagIDType) {
		this(antenna, accessPwd, memoryBank, ptr, length, data);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public WriteTag_6C() {

	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}
