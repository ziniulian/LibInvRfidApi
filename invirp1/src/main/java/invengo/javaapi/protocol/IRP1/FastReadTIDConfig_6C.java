package invengo.javaapi.protocol.IRP1;

/**
 * 快读TID开关
 *
 * @author dp732
 *
 */
public class FastReadTIDConfig_6C extends BaseMessage {

	/**
	 * @param infoType 信息类型
	 * @param infoParam 信息参数
	 */
	public FastReadTIDConfig_6C(byte infoType, byte infoParam) {
		super.msgBody = new byte[] { (byte) infoType, (byte) infoParam };
	}

	public FastReadTIDConfig_6C(){}

	@Override
	public FastReadTIDConfig6CReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new FastReadTIDConfig6CReceivedInfo(data);
	}

	public class FastReadTIDConfig6CReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		/**
		 *
		 */
		private static final long serialVersionUID = -6918266009854941847L;

		public FastReadTIDConfig6CReceivedInfo(byte[] buff) {
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
