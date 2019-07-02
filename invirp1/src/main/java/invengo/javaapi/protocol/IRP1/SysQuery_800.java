package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.receivedInfo.SysQuery800ReceivedInfo;

/**
 * 800系列查询读写器系统配置
 *
 * @author dp732
 *
 */
public class SysQuery_800 extends BaseMessage implements IEventHandle {

	byte parameter;

	/**
	 * @param parameter
	 *            查询读写器系统配置指令参数类型
	 */
	public SysQuery_800(byte parameter) {
		this.parameter = parameter;
		super.msgBody = new byte[2];
		super.msgBody[0] = parameter;
		super.msgBody[1] = 0x00;// 保留域默认为00H；
		if (parameter == 0x14 || parameter == 0x18) {
			super.onExecuted.add(this);
		}
	}

	/**
	 * @param parameter
	 *            查询读写器系统配置指令参数类型
	 * @param data
	 *            保留域默认为00H,扩展用
	 */
	public SysQuery_800(byte parameter, byte data) {
		this(parameter);
		super.msgBody[1] = data;
	}

	public SysQuery_800() {
	}

	void SysQuery_800_OnExecuted(Object sender, EventArgs e) {
		if (super.getStatusCode() == 0) {
			byte[] pData = this.getReceivedMessage().getQueryData();
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

	@Override
	public SysQuery800ReceivedInfo getReceivedMessage() {
		//		System.out.println(Util.convertByteArrayToHexString(super.rxData));
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new SysQuery800ReceivedInfo(data);
	}

	//	public class SysQuery800ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {
	//
	//		public SysQuery800ReceivedInfo(byte[] buff) {
	//			super(buff);
	//		}
	//
	//		public byte[] getQueryData() {
	//			return buff;
	//		}
	//	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		SysQuery_800_OnExecuted(sender, e);

	}

	public void eventHandle_executing(Object sender, EventArgs e) {

	}
}
