package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;

/**
 * 写EPC数据指令
 */

public class WriteEpc extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param pwd
	 *            标签访问密码
	 * @param epcData
	 *            写入标签EPC数据
	 */
	public WriteEpc(byte antenna, byte[] accessPwd, byte[] epcData) {
		byte[] epc = null;
		if (epcData.length % 2 == 0) {
			epc = epcData;
		} else {
			epc = new byte[epcData.length + 1];
			System.arraycopy(epcData, 0, epc, 0, epcData.length);
		}
		super.msgBody = new byte[1 + accessPwd.length + 1 + epc.length];
		super.msgBody[0] = antenna;// 天线
		System.arraycopy(accessPwd, 0, super.msgBody, 1, accessPwd.length);// 密码
		super.msgBody[1 + accessPwd.length] = (byte) (epc.length / 2);// EPC长度
		System.arraycopy(epc, 0, super.msgBody, 2 + accessPwd.length,
				epc.length);// EPC内容
	}

	public WriteEpc(byte antenna, byte[] accessPwd, byte[] epcData,
					byte[] tagID, MemoryBank tagIDType) {
		this(antenna, accessPwd, epcData);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public WriteEpc() {
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}