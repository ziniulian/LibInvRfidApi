package invengo.javaapi.protocol.IRP1;

/**
 * 500系列调整功率参数 设置/查询
 *
 * @author dp732
 *
 */
public class PowerParamConfig_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型：0设置，1查询
	 * @param infoParam
	 *            信息参数:其值需介于0x2000和0x2fff之间
	 */
	public PowerParamConfig_500(byte infoType, byte[] infoParam) {
		// 指令内容
		super.msgBody = new byte[3];
		super.msgBody[0] = infoType;
		if (infoParam != null && infoParam.length >= 2) {
			System.arraycopy(infoParam, 0, msgBody, 1, 2);
		}
	}

	public PowerParamConfig_500() {
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
			byte[] data = new byte[buff.length - 1];
			System.arraycopy(buff, 1, data, 0, data.length);
			return data;
		}
	}
}
