package invengo.javaapi.protocol.IRP1;

/**
 * 500系列韦根模式设置/查询
 *
 * @author dp732
 *
 */
public class WiegandMode_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型：0设置，1查询
	 * @param infoParam
	 *            信息参数：0韦根26工作模式，1韦根34工作模式
	 */
	public WiegandMode_500(byte infoType, byte infoParam) {
		// 指令内容
		super.msgBody = new byte[] { infoType, infoParam };
	}

	public WiegandMode_500() {
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
