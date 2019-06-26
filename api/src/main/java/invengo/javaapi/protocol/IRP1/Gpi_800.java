package invengo.javaapi.protocol.IRP1;

/**
 * 800系列IO输入查询
 *
 * @author dp732
 *
 */
public class Gpi_800 extends BaseMessage {

	public Gpi_800() {
		super.msgBody = new byte[1];
	}

	/**
	 * @param checkMode
	 *            查询方式(00H 立即返回当前IO输入状态)
	 */
	public Gpi_800(byte checkMode) {
		super.msgBody = new byte[] { checkMode };
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
