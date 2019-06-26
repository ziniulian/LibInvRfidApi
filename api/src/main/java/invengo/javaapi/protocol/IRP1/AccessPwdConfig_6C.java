package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 配置访问密码指令
 */
public class AccessPwdConfig_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param oldPwd
	 *            原访问密码
	 * @param newPwd
	 *            新访问密码
	 */
	public AccessPwdConfig_6C(byte antenna, byte[] oldPwd, byte[] newPwd) {
		super.msgBody = new byte[9];
		super.msgBody[0] = antenna;
		System.arraycopy(oldPwd, 0, msgBody, 1, 4);
		System.arraycopy(newPwd, 0, msgBody, 5, 4);
	}

	public AccessPwdConfig_6C(byte antenna, byte[] oldPwd, byte[] newPwd,
							  byte[] tagID, MemoryBank tagIDType) {
		this(antenna, oldPwd, newPwd);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public AccessPwdConfig_6C() {
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}
