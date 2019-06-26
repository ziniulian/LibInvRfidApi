package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 设置销毁密码指令
 */
public class KillPwdConfig_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            访问密码
	 * @param killPwd
	 *            新销毁密码
	 */
	public KillPwdConfig_6C(byte antenna, byte[] accessPwd, byte[] killPwd) {
		super.msgBody = new byte[9];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
		System.arraycopy(killPwd, 0, msgBody, 5, 4);
	}

	public KillPwdConfig_6C(byte antenna, byte[] accessPwd, byte[] killPwd,
							byte[] tagID, MemoryBank tagIDType) {
		this(antenna, accessPwd, killPwd);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public KillPwdConfig_6C() {
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}
