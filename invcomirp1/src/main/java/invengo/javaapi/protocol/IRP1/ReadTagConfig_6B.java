package invengo.javaapi.protocol.IRP1;

/**
 * 6B标签数据区信息参数设置/查询
 *
 * @author dp732
 *
 */
public class ReadTagConfig_6B extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型：0为设置，1为查询
	 * @param infoParam
	 *            信息参数：需要读取6B标签数据区的字节数
	 */
	public ReadTagConfig_6B(byte infoType, byte infoParam) {
		super.msgBody = new byte[] { infoType, infoParam };
	}

	public ReadTagConfig_6B(){}

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

		private byte infoType;
		private byte infoParam;

		public byte getInfoType() {
			if (buff != null && buff.length >= 1) {
				infoType = buff[0];
			}
			return this.infoType;
		}

		public byte getInfoParam() {
			if (buff != null && buff.length >= 2) {
				infoParam = buff[1];

			}
			return this.infoParam;
		}
	}

}
