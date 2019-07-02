package invengo.javaapi.protocol.IRP1;

/**
 * 500系列设置/查询数据的发送方式
 *
 * @author dp732
 *
 */
public class DataSentMode_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            操作类型：0,设置;1,查询（此时，状态域的一个字节无意义）
	 * @param infoParameter
	 *            状态： 0：数据通过韦根输出------天线1-韦根1；天线2-韦根2
	 *            1：数据通过韦根输出------天线1-韦根1；天线2-韦根1
	 *            2：数据通过韦根输出------天线1-韦根2；天线2-韦根2
	 *            3：数据通过韦根输出------天线1-韦根1、2；天线2-韦根1、2
	 *            4：数据通过继电器控制------天线1-继电1；天线2-继电2
	 *            5：数据通过继电器控制------天线1-继电1；天线2-继电1
	 *            6：数据通过继电器控制------天线1-继电2；天线2-继电2
	 *            7：数据通过继电器控制------天线1-继电1、2；天线2-继电1、2
	 */
	public DataSentMode_500(byte infoType, byte infoParameter) {
		super.msgBody = new byte[] { infoType, infoParameter };
	}

	public DataSentMode_500() {
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
