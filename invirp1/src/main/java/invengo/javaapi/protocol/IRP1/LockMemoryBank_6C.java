package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 标签锁状态配置指令
 */

public class LockMemoryBank_6C extends BaseMessage implements IEventHandle{


	/**
	 * @param antenna 天线端口
	 * @param accessPwd 标签访问密码
	 * @param lockType 锁操作类型
	 * @param memoryBank Bank类型
	 */
	public LockMemoryBank_6C(byte antenna, byte[] accessPwd, byte lockType, byte memoryBank)
	{
		super.msgBody = new byte[7];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
		super.msgBody[5] = lockType;
		super.msgBody[6] = memoryBank;
	}

	public LockMemoryBank_6C(byte antenna, byte[] accessPwd, byte lockType, byte memoryBank, byte[] tagID, MemoryBank tagIDType)
	{
		this(antenna, accessPwd, lockType, memoryBank);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public LockMemoryBank_6C() { }

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}