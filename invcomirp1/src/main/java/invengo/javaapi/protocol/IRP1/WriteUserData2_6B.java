package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.ErrorInfo;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 写用户数据区指令(写变长)
 */

public class WriteUserData2_6B extends BaseMessage implements IEventHandle {

	byte antenna;
	byte ptr;
	byte[] tagID;
	byte[] userData;
	int maxWriteLen = 108;// 单次最大写入字节数

	/**
	 * @param antenna
	 *            天线端口
	 * @param tagID
	 *            标签ID
	 * @param ptr
	 *            数据区写入首地址
	 * @param userData
	 *            写标签数据区数据
	 */
	public WriteUserData2_6B(byte antenna, byte[] tagID, byte ptr,
							 byte[] userData) {
		int ul = userData.length % maxWriteLen;
		if (ul == 0) {
			ul = maxWriteLen;
		}

		int len = 4 + tagID.length + ul;
		super.msgBody = new byte[len];
		super.msgBody[0] = antenna;
		super.msgBody[1] = 0x00;// 兼容所有标签
		System.arraycopy(tagID, 0, super.msgBody, 2, tagID.length);

		int c = userData.length / (maxWriteLen);
		if (userData.length % (maxWriteLen) == 0) {
			c--;
		}
		int p = (maxWriteLen) * c;
		super.msgBody[2 + tagID.length] = (byte) (ptr + p + 8);// 起始地址＋8
		super.msgBody[3 + tagID.length] = (byte) (ul);
		System.arraycopy(userData, p, super.msgBody, 4 + tagID.length,
				userData.length - p);

		if (userData.length > maxWriteLen) {
			this.antenna = antenna;
			this.tagID = tagID;
			this.ptr = ptr;
			this.userData = userData;
			super.onExecuting.add(this);
		}
	}

	public WriteUserData2_6B() {
	}

	void writeUserData2_6B_OnExecuting(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		int c = (userData.length / maxWriteLen) & 0xFF;
		if (userData.length % maxWriteLen == 0) {
			c--;
		}
		for (int i = 0; i < c; i++) {
			byte[] wd = new byte[maxWriteLen];
			byte p = (byte) (ptr + i * maxWriteLen);
			System.arraycopy(userData, i * maxWriteLen, wd, 0, maxWriteLen);
			WriteUserData2_6B w = new WriteUserData2_6B(antenna, tagID, p, wd);
			if (!reader.send(w)) {
				super.statusCode = w.statusCode;
				String key = String.format("%1$02X", super.statusCode);
				if (ErrorInfo.errMap.containsKey(key)) {
					super.errInfo = ErrorInfo.errMap.get(key);
				} else {
					super.errInfo = key;
				}
				throw new RuntimeException(super.errInfo);
			}
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		writeUserData2_6B_OnExecuting(sender,e);
	}

}
