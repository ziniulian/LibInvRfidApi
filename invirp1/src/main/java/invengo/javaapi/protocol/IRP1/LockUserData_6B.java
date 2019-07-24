package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 读用户数据区指令
 */

public class LockUserData_6B extends BaseMessage implements IEventHandle {

	byte antenna;
	byte ptr = (byte) 0xFF;
	byte[] tagID;
	byte[] lockInfo;
	byte[] lockResult;
	int count = 0;

	/**
	 * @param antenna
	 *            天线号
	 * @param tagID
	 *            标签ID
	 * @param lockInfo
	 *            锁定信息，长度与标签用户数据区长度一致，每个字节信息表示是否对该字节进行锁定操作，非0为锁定该位置的用户数据(注意：锁定后不能解锁)
	 */
	public LockUserData_6B(byte antenna, byte[] tagID, byte[] lockInfo) {
		lockResult = new byte[lockInfo.length];
		super.msgBody = new byte[3 + tagID.length];
		super.msgBody[0] = antenna;
		super.msgBody[1] = 0x00;// 标签类型：兼容所有标签
		System.arraycopy(tagID, 0, msgBody, 2, tagID.length);

		for (int i = 0; i < lockInfo.length; i++) {
			if (lockInfo[i] > 0) {
				count++;
				ptr = (byte) (i + 8);
			}
		}
		super.msgBody[msgBody.length - 1] = ptr;

		if (count > 1) {
			this.antenna = antenna;
			this.tagID = tagID;
			this.lockInfo = lockInfo;
			super.onExecuting.add(this);
		}

	}

	public LockUserData_6B() {
	}

	void lockUserData_6B_OnExecuting(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		int p = 0;
		for (int i = 0; i < lockInfo.length; i++) {
			if (lockInfo[i] != 0 && p + 1 < count) {
				p++;
				byte[] myLockInfo = new byte[lockInfo.length];
				myLockInfo[i] = 0x01;
				LockUserData_6B m = new LockUserData_6B(antenna, tagID,
						myLockInfo);
				if (!reader.send(m)) {
					super.statusCode = m.statusCode;
					String key = String.format("%1$02X", super.statusCode);
					if (Util.getErrorInfo(key)!=null) {
						errInfo = Util.getErrorInfo(key);
					} else {
						errInfo = key;
					}
					throw new RuntimeException(super.errInfo);
				}

			}
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		lockUserData_6B_OnExecuting(sender, e);

	}
}