package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.receivedInfo.ReadUserData6BReceivedInfo;

/**
 * 读用户数据区指令
 */

public class ReadUserData_6B extends BaseMessage implements IEventHandle {

	byte antenna;
	byte[] tagID;
	byte ptr;
	byte lastPtr;
	byte length;
	List<Byte> udBuff = new ArrayList<Byte>();
	int maxReadLen = 8;// 单次最大读取字节数

	byte dataLength = 8;// 实际取出的数据字节数

	/**
	 * @param antenna
	 *            天线端口
	 * @param tagID
	 *            标签ID
	 * @param ptr
	 *            标签数据区起始（1～107）
	 * @param length
	 *            标签数据读取长度
	 */
	public ReadUserData_6B(byte antenna, byte[] tagID, byte ptr, byte length) {
		if (ptr + length > 216 && ptr < 216){
			length = (byte) (216 - ptr);// 长度和起始地址超过标签容量则修改长度
		}
		super.msgBody = new byte[3 + tagID.length];
		super.msgBody[0] = antenna;
		super.msgBody[1] = 0x00;// 兼容所有标签
		System.arraycopy(tagID, 0, super.msgBody, 2, tagID.length);
		super.msgBody[2 + tagID.length] = (byte) (ptr + 8);// 起始地址＋8

		this.antenna = antenna;
		this.tagID = tagID;
		this.ptr = ptr;
		this.length = length;

		if (ptr >= 216){
			return;
		}// 起始地址大于216由基带返回错误信息

		if (216 - ptr < 8) {
			dataLength = (byte) (216 - ptr);
			super.msgBody[2 + tagID.length] = (byte) 216;// 起始地址208＋8
		}

		if (length > maxReadLen)// 保留最后一次读取参数
		{
			if (length % maxReadLen == 0) {
				byte p = (byte) (ptr + (length - maxReadLen) + 8);
				lastPtr = (byte) (p - 8);
				super.msgBody[2 + tagID.length] = p;// 起始地址＋8
			} else {
				dataLength = (byte) (length % maxReadLen);
				byte p = (byte) (ptr + (length - (length % maxReadLen)) + 8);
				lastPtr = (byte) (p - 8);
				if (p > 216){
					p = (byte) 216;
				}
				super.msgBody[2 + tagID.length] = p;// 起始地址＋8
			}
			super.onExecuting.add(this);
		}
	}

	public ReadUserData_6B() {
	}

	void readUserData_6B_OnExecuting(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		int c = (length / maxReadLen) & 0xFF;
		if (length % maxReadLen == 0) {
			c--;
		}
		for (int i = 0; i < c; i++) {
			byte currentPtr = (byte) (ptr + i * maxReadLen);
			ReadUserData_6B r = new ReadUserData_6B(antenna, tagID, currentPtr, (byte) maxReadLen);
			if (reader.send(r)) {
				for (byte b : r.getReceivedMessage().getUserData()) {
					udBuff.add(b);
				}
			} else {
				super.statusCode = r.statusCode;
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

	public ReadUserData6BReceivedInfo getReceivedMessage() {

		byte[] data = Decode.getRxMessageData(super.rxData);
		if (udBuff.size() > 0) {
			byte[] old = new byte[udBuff.size()];
			for(int i = 0; i < old.length; i++){
				old[i] = udBuff.get(i);
			}
			byte[] d = new byte[old.length + data.length - (8 - dataLength)];
			d[0] = data[0];
			d[1] = data[1];
			d[2] = data[2];
			int offer = 0;
			if (lastPtr > 208)
				offer = lastPtr - 208;
			System.arraycopy(old, 0, d, 3, old.length);
			System.arraycopy(data, 3 + offer, d, 3 + old.length, dataLength);
			data = d;
		}
		if (data == null) {
			return null;
		}
		return new ReadUserData6BReceivedInfo(data);
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		readUserData_6B_OnExecuting(sender, e);
	}

}
