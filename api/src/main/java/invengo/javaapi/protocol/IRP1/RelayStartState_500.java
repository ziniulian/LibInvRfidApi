package invengo.javaapi.protocol.IRP1;

/**
 * 500系列设置/查询继电器的初始状态
 *
 * @author dp732
 *
 */
public class RelayStartState_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            操作类型：0,设置;1,查询（此时，状态域的一个字节无意义）
	 * @param infoParameter
	 *            状态： 0： 继电器的常态为断开;1： 继电器的常态为接通
	 */
	public RelayStartState_500(byte infoType, byte infoParameter) {
		super.msgBody = new byte[2];
		super.msgBody[0] = infoType;
		super.msgBody[1] = infoParameter;
	}

	public RelayStartState_500() {
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
