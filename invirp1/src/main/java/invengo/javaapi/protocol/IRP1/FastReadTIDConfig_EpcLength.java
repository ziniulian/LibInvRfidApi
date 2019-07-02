package invengo.javaapi.protocol.IRP1;

/**
 * 快读TID时EPC相对长度
 *
 * @author dp732
 *
 */
public class FastReadTIDConfig_EpcLength extends BaseMessage {

	/**
	 * @param infoType 信息类型
	 * @param infoParam 信息参数
	 */
	public FastReadTIDConfig_EpcLength(byte infoType, byte[] infoParam) {
		int len = 1;
		if (infoParam != null) {
			len += infoParam.length;
		}
		super.msgBody = new byte[len];
		super.msgBody[0] = infoType;
		if (infoParam != null) {
			System.arraycopy(infoParam, 0, super.msgBody, 1, infoParam.length);
		}
	}

	public FastReadTIDConfig_EpcLength(){}

	public FastReadTIDConfigEpcLengthReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new FastReadTIDConfigEpcLengthReceivedInfo(data);
	}

	public class FastReadTIDConfigEpcLengthReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		private static final long serialVersionUID = 374562272860713413L;

		public FastReadTIDConfigEpcLengthReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte[] getQueryData()
		{
			byte[] data = new byte[buff.length - 1];
			System.arraycopy(buff, 1, data, 0, data.length);
			return data;
		}
	}
}