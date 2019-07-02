package invengo.javaapi.protocol.IRP1;

/**
 * 其他时间设置/查询
 *
 * @author dp732
 *
 */
public class ReaderInterval_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型
	 * @param infoParam
	 *            信息参数（3字节） 信息参数定义： 信息参数1：与服务器的连接时间 信息参数2：标签数据发送时间
	 *            信息参数3：授时请求发送时间
	 */
	public ReaderInterval_500(byte infoType, byte[] infoParam) {
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

	public ReaderInterval_500() {

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
		 * @return 信息参数 信息参数定义： 信息参数1：与服务器的连接时间 信息参数2： 标签数据发送时间 信息参数3： 授时请求发送时间
		 */
		public byte[] getInfoParam() {
			byte[] table = new byte[3];
			System.arraycopy(buff, 1, table, 0, table.length);
			return table;
		}
	}
}
