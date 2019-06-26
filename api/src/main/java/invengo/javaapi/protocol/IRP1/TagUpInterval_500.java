package invengo.javaapi.protocol.IRP1;

/**
 * 相同标签上传时间设置/查询
 *
 * @author dp732
 *
 */
public class TagUpInterval_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型
	 * @param infoParam
	 *            信息参数
	 */
	public TagUpInterval_500(byte infoType, byte[] infoParam) {
		super.msgBody = new byte[3];
		super.msgBody[0] = infoType;
		super.msgBody[1] = infoParam[0];
		super.msgBody[2] = infoParam[1];
	}

	public TagUpInterval_500() {
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
		 * @return 信息类型
		 */
		public byte getInfoType() {
			return buff[0];
		}

		/**
		 * @return 信息参数
		 */
		public byte[] getInfoParam() {
			byte[] table = new byte[2];
			System.arraycopy(buff, 1, table, 0, table.length);
			return table;
		}

		/**
		 * @return 间隔时间
		 */
		public int getIntervalSecond() {
			byte[] info = getInfoParam();
			if (info.length == 2) {
				return ((int) info[0] * 256 + info[1]);
			}
			return -1;
		}

	}
}
