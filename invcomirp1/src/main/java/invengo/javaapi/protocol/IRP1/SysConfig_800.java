package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 800系列配置读写器系统配置
 *
 * @author dp732
 *
 */
public class SysConfig_800 extends BaseMessage implements IEventHandle {

	byte parameter;
	byte[] pData;

	/**
	 * @param parameter
	 *            配置读写器系统配置指令参数类型
	 * @param data
	 *            配置数据
	 */
	public SysConfig_800(byte parameter, byte[] data) {
		// 指令内容
		this.parameter = parameter;
		this.pData = data;
		if (data == null) {
			throw new IllegalArgumentException();
		}
		super.msgBody = new byte[data.length + 2];
		super.msgBody[0] = parameter;
		super.msgBody[1] = (byte) data.length;// 参数长度
		System.arraycopy(data, 0, msgBody, 2, data.length);
		if (parameter == 0x14 || parameter == 0x18) {
			super.onExecuted.add(this);
		}
	}

	public SysConfig_800() {
	}

	void SysConfig_800_OnExecuted(Object sender, EventArgs e) {
		if (super.getStatusCode() == 0) {
			if (pData != null && pData.length > 0) {
				Reader reader = (Reader) sender;
				if (parameter == 0x14) {
					if (pData[0] == 1) {
						reader.isRssiEnable = true;
					} else {
						reader.isRssiEnable = false;
					}
				}
				if (parameter == 0x18) {
					if (pData[0] == 1) {
						reader.isUtcEnable = true;
					} else {
						reader.isUtcEnable = false;
					}
				}
			}
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		SysConfig_800_OnExecuted(sender, e);

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
	}
}
