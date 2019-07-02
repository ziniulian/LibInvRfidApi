package invengo.javaapi.protocol.IRP1;

/**
 * 500系列门限值（边界比）设置/查询
 *
 * @author dp732
 *
 */
public class RssiLimitConfig_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型，同工作模式信息类型
	 * @param infoParameter
	 *            信息参数，查询时可以传null，设置时需要输入两字节的强度值
	 */
	public RssiLimitConfig_500(byte infoType, byte[] infoParameter) {
		super.msgBody = new byte[3];
		super.msgBody[0] = infoType;
		if (infoParameter != null && infoParameter.length == 2) {
			System.arraycopy(infoParameter, 0, msgBody, 1, 2);
		}
	}

	public RssiLimitConfig_500() {
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
