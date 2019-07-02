package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

import com.invengo.lib.diagnostics.InvengoLog;

/**
 * 写用户数据区指令
 */

public class WriteUserData_6C extends BaseMessage implements IEventHandle {

	byte antenna;
	int ptr;
	byte[] pwd;
	int udLen;
	byte[] userData;
	int maxWriteLen = 16;// 单次最大写入word数

	int eventFlag = 0;

	/**
	 * @param antenna
	 *            天线端口
	 * @param pwd
	 *            标签访问密码
	 * @param ptr
	 *            标签数据区首地址
	 * @param userData
	 *            写标签数据区数据
	 */
	public WriteUserData_6C(byte antenna, byte[] pwd, int ptr, byte[] userData) {
		if (pwd == null || userData == null) {
			throw new IllegalArgumentException();
		}
		udLen = userData.length + (userData.length % 2);
		if (udLen > maxWriteLen * 2) {
			eventFlag = 2;
			this.antenna = antenna;
			this.pwd = pwd;
			this.ptr = ptr;
			this.userData = userData;
			int ul = udLen % (maxWriteLen * 2);
			if (ul == 0) {
				ul = maxWriteLen * 2;
			}
			int c = userData.length / (maxWriteLen * 2);
			if (userData.length % (maxWriteLen * 2) == 0) {
				c--;
			}
			byte[] ptrArray = EVB.convertToEvb((ptr + maxWriteLen * c) & 0xFF);
			int len = 6 + ul + ptrArray.length;

			super.msgBody = new byte[len];
			super.msgBody[0] = antenna;
			System.arraycopy(pwd, 0, super.msgBody, 1, pwd.length);

			int p = (maxWriteLen * 2) * c;

			System.arraycopy(ptrArray, 0, super.msgBody, 1 + pwd.length, ptrArray.length);
			super.msgBody[1 + pwd.length + ptrArray.length] = (byte) ((userData.length - p) / 2);

			System.arraycopy(userData, p, super.msgBody, 2 + pwd.length + ptrArray.length, userData.length - p);
			super.onExecuting.add(this);
		} else {
			byte[] ptrArray = EVB.convertToEvb(ptr);
			int len = 6 + udLen + ptrArray.length;
			super.msgBody = new byte[len];

			super.msgBody[0] = antenna;
			System.arraycopy(pwd, 0, super.msgBody, 1, pwd.length);
			System.arraycopy(ptrArray, 0, super.msgBody, 1 + pwd.length, ptrArray.length);
			super.msgBody[1 + pwd.length + ptrArray.length] = (byte) (userData.length / 2 + (userData.length % 2));
			System.arraycopy(userData, 0, super.msgBody, 2 + pwd.length + ptrArray.length, userData.length);
		}
	}

	public WriteUserData_6C(byte antenna, byte[] pwd, byte ptr,
							byte[] userData, byte[] tagID, MemoryBank tagIDType) {
		this(antenna, pwd, ptr, userData);
		//		eventFlag = 1;
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		udLen = userData.length + (userData.length % 2);
		if(udLen <= maxWriteLen * 2){
			eventFlag = 1;
			super.onExecuting.add(this);
		}
	}

	public WriteUserData_6C() {
		eventFlag = 0;
	}

	void writeUserData_6C_OnExecuting(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		int c = (userData.length / (maxWriteLen * 2)) & 0xFF;
		if (userData.length % (maxWriteLen * 2) == 0) {
			c--;
		}
		for (int i = 0; i < c; i++) {
			super.selectTag(sender, e);
			byte[] wd = new byte[maxWriteLen * 2];
			int p = (ptr + i * maxWriteLen) & 0xFF;
			System.arraycopy(userData, i * maxWriteLen * 2, wd, 0, maxWriteLen * 2);
			InvengoLog.i("Test", Util.convertByteArrayToHexString(wd));
			WriteUserData_6C w = new WriteUserData_6C(antenna, pwd, p, wd);
			if (!reader.send(w)) {
				super.statusCode = w.statusCode;
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

	public void eventHandle(Object sender, EventArgs e) {

	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		switch (eventFlag) {
			case 0:
				// do nothing
				break;
			case 1:
				super.selectTag(sender, e);
				break;
			case 2:
				writeUserData_6C_OnExecuting(sender, e);
				super.selectTag(sender, e);
				break;
			default:
				break;
		}
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		switch (eventFlag) {
			case 0:
				// do nothing
				break;
			case 1:
				super.selectTag(sender, e);
				break;
			case 2:
				writeUserData_6C_OnExecuting(sender, e);
				super.selectTag(sender, e);
				break;
			default:
				break;
		}
	}

}
