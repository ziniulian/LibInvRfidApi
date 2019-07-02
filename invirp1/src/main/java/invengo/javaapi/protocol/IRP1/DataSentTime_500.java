package invengo.javaapi.protocol.IRP1;

/**
 * 500系列设置/查询数据发送时间
 *
 * @author dp732
 *
 */
public class DataSentTime_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            操作类型：0,设置;1,查询（此时，时间域的两个字节无意义）
	 * @param infoParameter
	 *            时间
	 */
	public DataSentTime_500(byte infoType, byte[] infoParameter) {
		super.msgBody = new byte[3];
		super.msgBody[0] = infoType;
		if (infoParameter != null && infoParameter.length >= 2) {
			System.arraycopy(infoParameter, 0, msgBody, 1, 2);
		}
	}

	public DataSentTime_500() {
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
