package invengo.javaapi.protocol.IRP1;

/**
 * 500系列工作模式设置/查询
 *
 * @author dp732
 *
 */
public class WorkModeSet_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            工作模式信息类型
	 * @param infoParameter
	 *            工作模式信息类型参数
	 */
	public WorkModeSet_500(byte infoType, byte infoParameter) {
		// 指令内容
		super.msgBody = new byte[] { infoType, infoParameter };
	}

	public WorkModeSet_500() {
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
