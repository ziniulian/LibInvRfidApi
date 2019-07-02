package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;
import invengo.javaapi.protocol.IRP1.Decode;
import invengo.javaapi.protocol.receivedInfo.ReadUserDataNonFixed6BReceivedInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 读用户数据区指令(读变长)
 */

public class ReadUserData2_6B extends BaseMessage implements IEventHandle {

	byte antenna;
	byte[] tagID;
	byte ptr;
	byte length;
	List<Byte> udBuff = new ArrayList<Byte>();
	int maxReadLen = 108;// 单次最大读取字节数

	/**
	 * @param antenna
	 *            天线端口
	 * @param ptr
	 *            标签数据区首地址（EVB格式）
	 * @param length
	 *            标签数据读取长度
	 */
	public ReadUserData2_6B(byte antenna, byte[] tagID, byte ptr, byte length) {
		super.msgBody = new byte[4 + tagID.length];
		super.msgBody[0] = antenna;
		super.msgBody[1] = 0x00;// 兼容所有标签
		System.arraycopy(tagID, 0, super.msgBody, 2, tagID.length);
		if (length % maxReadLen == 0) {
			super.msgBody[2 + tagID.length] = (byte) (ptr
					+ (length - maxReadLen) + 8);// 起始地址＋8
			super.msgBody[3 + tagID.length] = (byte) maxReadLen;
		} else {
			super.msgBody[2 + tagID.length] = (byte) (ptr
					+ (length - (length % maxReadLen)) + 8);// 起始地址＋8
			super.msgBody[3 + tagID.length] = (byte) (length % maxReadLen);
		}

		if (ptr >= 216) {
			return;// 起始地址大于216由基带返回错误信息
		}

		if (length > maxReadLen) {
			this.antenna = antenna;
			this.tagID = tagID;
			this.ptr = ptr;
			this.length = length;
			super.onExecuting.add(this);
		}
	}

	public ReadUserData2_6B() {
	}

	void readUserData2_6B_OnExecuting(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		int c = (length / maxReadLen) & 0xFF;
		if (length % maxReadLen == 0) {
			c--;
		}
		for (int i = 0; i < c; i++) {
			ReadUserData2_6B r = new ReadUserData2_6B(antenna, tagID,
					(byte) (ptr + i * maxReadLen), (byte) maxReadLen);
			if (reader.send(r)) {
				for (byte b : r.getReceivedMessage().getUserData()) {
					udBuff.add(b);
				}
			} else {
				super.statusCode = r.statusCode;
				super.errInfo = Util.getErrorInfo(String.format("%1$02X", super
						.getStatusCode()));
				throw new RuntimeException(super.errInfo);
			}
		}
	}

	public ReadUserDataNonFixed6BReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (udBuff.size() > 0) {
			byte[] old = new byte[udBuff.size()];
			for(int i = 0; i < old.length; i++){
				old[i] = udBuff.get(i);
			}

			byte[] d = new byte[data.length + old.length];
			System.arraycopy(data, 0, d, 0, 4);
			System.arraycopy(old, 0, d, 4, old.length);
			System.arraycopy(data, 4, d, 4 + old.length, data.length - 4);
			data = d;
		}
		if (data == null)
			return null;
		return new ReadUserDataNonFixed6BReceivedInfo(data);
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		readUserData2_6B_OnExecuting(sender, e);

	}

}
