package invengo.javaapi.protocol.IRP1;

/**
 * 定制业务数据
 *
 * @author dp732
 *
 */
public class CustomBusinessData extends BaseMessage {

	/**
	 * @param type
	 *            业务类型
	 * @param msgID
	 *            业务消息ID
	 */
	public CustomBusinessData(byte[] type, byte[] msgID) {
		isReturn = false;
		super.msgBody = new byte[4];
		super.msgBody[0] = type[0];
		super.msgBody[1] = type[1];
		super.msgBody[2] = msgID[0];
		super.msgBody[3] = msgID[1];

	}

	public CustomBusinessData() {

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

		/**
		 * 获取业务类型
		 *
		 * @return
		 */
		public byte[] getType() {
			byte[] type = null;
			if (buff != null && buff.length >= 5) {
				type = new byte[2];
				System.arraycopy(buff, 0, type, 0, 2);
			}
			return type;
		}

		/**
		 * 获取业务消息ID
		 *
		 * @return
		 */
		public byte[] getMessageID() {
			byte[] msgID = null;
			if (buff != null && buff.length >= 5) {
				msgID = new byte[2];
				System.arraycopy(buff, 2, msgID, 0, 2);
			}
			return msgID;
		}

		/**
		 * 获取业务数据类型
		 *
		 * @return
		 */
		public byte getDataType() {
			byte datatype = (byte) 0xff;
			if (buff != null && buff.length >= 5) {
				datatype = buff[4];
			}
			return datatype;
		}

		/**
		 * 获取业务数据
		 *
		 * @return
		 */
		public byte[] getData() {
			byte[] data = null;
			if (buff != null && buff.length > 5) {
				data = new byte[buff.length - 5];
				System.arraycopy(buff, 5, data, 0, data.length);
			}
			return data;
		}

	}
}
