package invengo.javaapi.protocol.IRP1;

/**
 * 定制业务控制
 *
 * @author dp732
 *
 */
public class CustomBusinessControl extends BaseMessage {

	/**
	 * @param type
	 *            业务类型
	 * @param param
	 *            业务指令字
	 * @param data
	 *            业务参数数据
	 */
	public CustomBusinessControl(byte[] type, byte param, byte[] data) {
		if (type == null || type.length != 2) {
			throw new IllegalArgumentException();
		}
		if (data == null || data.length > 0xffff) {
			throw new IllegalArgumentException();
		}
		int len = 5;
		if (data != null)
			len += data.length;
		super.msgBody = new byte[len];
		super.msgBody[0] = type[0];
		super.msgBody[1] = type[1];
		super.msgBody[2] = param;
		super.msgBody[3] = (byte) (data.length / 256);
		super.msgBody[4] = (byte) (data.length % 256);
		System.arraycopy(data, 0, super.msgBody, 5, data.length);
	}

	public CustomBusinessControl() {

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
		 * 获取执行结果
		 *
		 * @return
		 */
		public byte getResult() {
			byte result = (byte) 0xff;
			if (buff != null && buff.length >= 4) {
				result = buff[3];
			}
			return result;
		}

	}
}
