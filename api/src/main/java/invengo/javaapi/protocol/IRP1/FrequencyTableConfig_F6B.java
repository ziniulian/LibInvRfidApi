package invengo.javaapi.protocol.IRP1;

/**
 *
 * F6B 配置读写器系统配置指令
 *
 * @author dp732
 *
 */
public class FrequencyTableConfig_F6B extends BaseMessage {

	/**
	 *
	 * @param infoType
	 *            系统配置信息类型：00设置，01查询
	 * @param param
	 *            信息参数（2字节）
	 */
	public FrequencyTableConfig_F6B(byte infoType, byte[] param) {
		if (param == null) {
			param = new byte[2];
		}
		super.msgBody = new byte[1 + param.length];
		super.msgBody[0] = infoType;
		System.arraycopy(param, 0, msgBody, 1, param.length);
	}

	public FrequencyTableConfig_F6B() {

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

		public byte getInfoType() {
			return buff[0];
		}

		public byte[] getFrequencyTable() {
			byte[] table = new byte[buff.length - 1];
			System.arraycopy(buff, 1, table, 0, table.length);
			return table;
		}

	}
}
