package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.ErrorInfo;
import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 读用户数据区指令
 */

public class ReadUserData_6C extends BaseMessage implements IEventHandle {

	byte antenna;
	int ptr;
	byte length;
	List<Byte> udBuff = new ArrayList<Byte>();
	int maxReadLen = 16;// 单次最大写入word数

	int eventFlag = 0;

	/**
	 * @param antenna
	 *            天线端口
	 * @param ptr
	 *            标签数据区首地址（EVB格式）
	 * @param length
	 *            标签数据读取长度
	 */
	public ReadUserData_6C(byte antenna, int ptr, byte length) {
		int myPtr = ptr;
		byte myLength = length;
		if (length > maxReadLen)// 保留最后一次读取参数
		{
			this.antenna = antenna;
			this.ptr = ptr;
			this.length = length;
			if (length % maxReadLen == 0) {
				myPtr = (ptr + (length - maxReadLen)) & 0xFF;
				myLength = (byte) maxReadLen;
			} else {
				myPtr = (ptr + (length - (length % maxReadLen))) & 0xFF;
				myLength = (byte) (length % maxReadLen);
			}
			super.onExecuting.add(this);
		}

		byte[] ptrArray = EVB.convertToEvb(myPtr);
		super.msgBody = new byte[6 + ptrArray.length];
		super.msgBody[0] = antenna;
		// 1-4字节为保留域
		System.arraycopy(ptrArray, 0, msgBody, 5, ptrArray.length);
		super.msgBody[5 + ptrArray.length] = myLength;
	}

	public ReadUserData_6C(byte antenna, int ptr, byte length, byte[] tagID,
						   MemoryBank tagIDType) {
		this(antenna, ptr, length);
		eventFlag = 2;
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public ReadUserData_6C() {
		eventFlag = 0;
	}

	void readUserData_6C_OnExecuting(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		int c = (length / maxReadLen) & 0xFF;
		if (length % maxReadLen == 0) {
			c--;
		}
		for (int i = 0; i < c; i++) {
			super.selectTag(sender, e);
			int p = (ptr + i * maxReadLen) & 0xFF;
			ReadUserData_6C r = new ReadUserData_6C(antenna, p,
					(byte) maxReadLen);
			if (reader.send(r)) {
				for (byte b : r.getReceivedMessage().userdata) {
					udBuff.add(b);
				}
			} else {
				super.statusCode = r.statusCode;
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

	public ReceivedInfo getReceivedMessage() {

		byte[] data = Decode.getRxMessageData(super.rxData);
		if (udBuff.size() > 0) {
			byte[] d = new byte[data.length + udBuff.size()];
			d[0] = data[0];
			System.arraycopy(udBuff.toArray(), 0, d, 1, udBuff.size());
			System.arraycopy(data, 1, d, 1 + udBuff.size(), data.length - 1);
			data = d;
		}
		if (data == null)
			return null;
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte getAntenna() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;

		}

		private byte[] userdata = null;

		public byte[] getUserData() {
			if (buff != null && buff.length >= 2) {
				userdata = new byte[buff.length - 1];
				System.arraycopy(buff, 1, userdata, 0, userdata.length);
			}
			return userdata;
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		switch (eventFlag) {
			case 0:
				// do nothing
				break;
			case 1:
				readUserData_6C_OnExecuting(sender, e);
				break;
			case 2:
				readUserData_6C_OnExecuting(sender, e);
				super.selectTag(sender, e);
				break;
			default:
				break;
		}
	}

}
