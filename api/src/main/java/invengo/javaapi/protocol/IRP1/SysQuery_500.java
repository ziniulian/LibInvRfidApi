package invengo.javaapi.protocol.IRP1;

/**
 * 500系列查询读写器系统配置指令
 *
 * @author dp732
 *
 */
public class SysQuery_500 extends BaseMessage {

	/**
	 * @param infoType 系统配置信息类型
	 * @param infoLength 系统配置信息类型长度
	 */
	public SysQuery_500(byte infoType, byte infoLength) {
		super.msgBody = new byte[2];
		super.msgBody[0] = infoType;
		super.msgBody[1] = infoLength;
	}

	public SysQuery_500() {
	}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte[] getQueryData() {
			return buff;
		}
	}
}
