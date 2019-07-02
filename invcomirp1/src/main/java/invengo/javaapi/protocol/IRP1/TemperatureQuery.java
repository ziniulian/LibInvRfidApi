package invengo.javaapi.protocol.IRP1;

/**
 * 温度检测指令
 *
 * @author dp732
 *
 */
public class TemperatureQuery extends BaseMessage {

	public TemperatureQuery() {

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

		public String toTemperatureString() {
			String t = "";
			if (getQueryData() != null && getQueryData().length == 2) {
				byte bt = getQueryData()[0];
				if (bt >= 0x80) {
					bt--;
					bt = (byte) (bt ^ 0xff);
					t = "-" + Byte.toString(bt);
				} else {
					t = Byte.toString(bt);
				}
				if (getQueryData()[1] == 0x80) {
					t += ".5";
				}
				t += "℃";
			}
			return t;
		}

	}
}
