package invengo.javaapi.protocol.IRP1;

/**
 * 500系列频率查询
 *
 * @author zxq943
 *
 */

public class FreqQuery_500 extends BaseMessage {

	/**
	 *
	 * @param infoParam
	 *            信息参数
	 */
	public FreqQuery_500(byte[] infoParam) {

		super.msgBody = infoParam;
	}

	public FreqQuery_500() {
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
