package invengo.javaapi.protocol.IRP1;

/**
 * 查询电压值
 *
 * @author dp732
 *
 */
public class ReaderVoltage extends BaseMessage {

	public ReaderVoltage() {

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
		 * 获取电压值
		 *
		 * @return
		 */
		public double getVoltageValue() {
			double v = 0;
			if (buff != null && buff.length >= 2) {
				v = buff[0] * 256 + buff[1];
				v = v / 255 * 2.75 * 11 / 4;
			}
			return v;
		}
	}
}
