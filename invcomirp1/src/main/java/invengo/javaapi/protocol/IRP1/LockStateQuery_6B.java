package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.ErrorInfo;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

public class LockStateQuery_6B extends BaseMessage implements IEventHandle {

	byte antenna;
	byte ptr = (byte) 0xFF;
	byte[] tagID;
	byte[] queryInfo;
	byte[] lockState;
	int count = 0;

	/**
	 * @param antenna
	 *            天线号
	 * @param tagID
	 *            标签ID号
	 * @param queryInfo
	 *            查询信息，长度与标签用户数据区长度一致，每个字节信息表示对该字节是否执行查询操作，非0为执行操作
	 */
	public LockStateQuery_6B(byte antenna, byte[] tagID, byte[] queryInfo) {
		lockState = new byte[queryInfo.length];

		super.msgBody = new byte[3 + tagID.length];
		super.msgBody[0] = antenna;
		super.msgBody[1] = 0x00;// 标签类型：兼容所有标签
		System.arraycopy(tagID, 0, msgBody, 2, tagID.length);
		for (int i = 0; i < queryInfo.length; i++) {
			if (queryInfo[i] > 0) {
				count++;
				ptr = (byte) (i + 8);
			}
		}
		super.msgBody[msgBody.length - 1] = ptr;

		if (count > 1) {
			this.antenna = antenna;
			this.tagID = tagID;
			this.queryInfo = queryInfo;
			super.onExecuting.add(this);
		}
	}

	public LockStateQuery_6B() {
	}

	void LockStateQuery_6B_OnExecuting(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;
		int p = 0;
		for (int i = 0; i < queryInfo.length; i++) {
			if (queryInfo[i] != 0 && p + 1 < count) {
				p++;
				byte[] myQueryInfo = new byte[queryInfo.length];
				myQueryInfo[i] = 0x01;
				LockStateQuery_6B m = new LockStateQuery_6B(antenna, tagID,
						myQueryInfo);
				if (!reader.send(m)) {
					super.statusCode = m.statusCode;
					String key = String.format("%1$02X", super.statusCode);
					if (ErrorInfo.errMap.containsKey(key)) {
						super.errInfo = ErrorInfo.errMap.get(key);
					} else {
						super.errInfo = key;
					}
					throw new RuntimeException(super.errInfo);
				} else {
					byte[] data = Decode.getRxMessageData(m.rxData);
					if (data != null && data.length >= 4) {
						lockState[(data[2] - 8) & 0XFF] = data[3];
					}
				}
			}
		}
	}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data != null && data.length >= 4) {
			lockState[(data[2] - 8) & 0XFF] = data[3];
		}
		return new ReceivedInfo(lockState);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte[] getLockResult() {
			return buff;
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		LockStateQuery_6B_OnExecuting(sender, e);
	}
}
