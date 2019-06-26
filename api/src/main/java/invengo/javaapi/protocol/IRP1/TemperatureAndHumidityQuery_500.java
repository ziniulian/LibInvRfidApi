package invengo.javaapi.protocol.IRP1;

/**
 * 500系列温度/湿度检测指令
 *
 * @author dp732
 *
 */
public class TemperatureAndHumidityQuery_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型:温度(0x00),湿度(0x01)
	 */
	public TemperatureAndHumidityQuery_500(Byte infoType) {
		super.msgBody = new byte[] { infoType };
	}

	public TemperatureAndHumidityQuery_500() {
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

		public String ToTemperatureString() {
			String t = "";
			if (getQueryData() != null && getQueryData().length > 0) {
				t = (getQueryData()[0] & 0xFF - 40) + "℃";
			}
			return t;
		}

		public String ToHumidityString() {
			String h = "";
			if (getQueryData() != null && getQueryData().length > 0) {
				h = (getQueryData()[0] & 0xFF - 10) + "%";
			}
			return h;
		}

	}
}
