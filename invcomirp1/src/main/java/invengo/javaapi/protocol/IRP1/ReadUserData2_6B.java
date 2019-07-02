package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.ErrorInfo;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

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

		if (ptr >= 216){
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
		if (length % maxReadLen == 0){
			c--;
		}
		for (int i = 0; i < c; i++) {
			ReadUserData2_6B r = new ReadUserData2_6B(antenna, tagID,
					(byte) (ptr + i * maxReadLen), (byte) maxReadLen);
			if (reader.send(r)) {
				for (byte b : r.getReceivedMessage().userdata) {
					udBuff.add(b);
				}
			} else {
				super.statusCode = r.statusCode;
				super.errInfo = ErrorInfo.errMap.get(
						String.format("%1$02X", super.getStatusCode()));
				throw new RuntimeException(super.errInfo);
			}
		}
	}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (udBuff.size() > 0) {
			byte[] d = new byte[data.length + udBuff.size()];
			System.arraycopy(data, 0, d, 0, 4);
			System.arraycopy(udBuff.toArray(), 0, d, 4, udBuff.size());
			System.arraycopy(data, 4, d, 4 + udBuff.size(), data.length - 4);
			data = d;
		}
		if (data == null)
			return null;
		return new ReadUserData2_6B.ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte getAntenna() {
			if (buff != null && buff.length >= 1)
				return buff[0];
			return 0x00;
		}

		private byte[] userdata = null;

		public byte[] getUserData() {
			if (buff != null && buff.length >= 5) {
				userdata = new byte[buff.length - 4];
				System.arraycopy(buff, 4, userdata, 0, userdata.length);
			}
			return userdata;
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		readUserData2_6B_OnExecuting(sender, e);

	}

}
