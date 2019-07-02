package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 获取销毁密码
 *
 * @author yxx981
 *
 */
public class GetTagKillPwd_6C extends BaseMessage implements IEventHandle {

	public GetTagKillPwd_6C(byte antenna, byte[] accessPwd) {
		super.msgBody = new byte[5];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
	}

	public GetTagKillPwd_6C(byte antenna, byte[] accessPwd, byte[] tagID,
							MemoryBank tagIDType) {
		this(antenna, accessPwd);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public GetTagKillPwd6CReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new GetTagKillPwd6CReceivedInfo(data);
	}

	public class GetTagKillPwd6CReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		private static final long serialVersionUID = 5606065298973068461L;

		public GetTagKillPwd6CReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte getAntenna() {
			byte a = 0x01;
			if (buff != null && buff.length >= 1) {
				a = buff[0];
			}
			return a;
		}

		public byte[] getKillPwd() {
			byte[] pwd = new byte[4];
			if (buff != null && buff.length >= 5) {
				// 数据内容
				System.arraycopy(buff, 1, pwd, 0, 4);
			}
			return pwd;
		}

	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}
}
