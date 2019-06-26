package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * EAS标志配置指令
 *
 * @author dp732
 *
 */
public class EasConfig_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            访问密码
	 * @param flag
	 *            EAS标志配置
	 */
	public EasConfig_6C(byte antenna, byte[] accessPwd, byte flag) {
		super.msgBody = new byte[6];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, 4);
		super.msgBody[5] = flag;
	}

	public EasConfig_6C(byte antenna, byte[] accessPwd, byte flag,
						byte[] tagID, MemoryBank tagIDType) {
		this(antenna, accessPwd, flag);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public EasConfig_6C() {

	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}
}
