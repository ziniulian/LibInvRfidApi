package invengo.javaapi.protocol.IRP1;

/**
 * 500系列触发读卡模式设置/查询
 *
 * @author dp732
 *
 */
public class ReadModeTrigger_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型：0设置，1查询
	 * @param infoParam
	 *            信息参数： 0不需要触发读卡； 1输入IO口1触发读卡； 2输入IO口2触发读卡； 3输入IO口1或2都可以触发读卡
	 *
	 */
	public ReadModeTrigger_500(byte infoType, byte infoParam) {
		// 指令内容
		super.msgBody = new byte[] { infoType, infoParam };
	}

	public ReadModeTrigger_500() {

	}

	public ReceivedInfo getReceivedMessage(){
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
