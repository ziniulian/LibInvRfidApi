package invengo.javaapi.protocol.IRP1;

class RXD_ID_UserData_6B_2 extends BaseMessageNotification {

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

		public byte getAntenna() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;
		}

		public byte getTagType() {
			if (buff != null && buff.length >= 2) {
				return buff[1];
			}
			return 0x00;
		}

		private byte[] tid = null;

		public byte[] getID() {
			if (buff != null && buff.length >= 10) {
				tid = new byte[8];
				System.arraycopy(buff, 2, tid, 0, tid.length);
			}
			return tid;
		}

		private int udLen = -1;
		private byte[] ud = null;

		public byte[] getUserData() {
			if (buff != null && buff.length >= 11) {
				udLen = buff[10];
				if (udLen > 0 && buff.length >= udLen + 11) {
					ud = new byte[udLen];
					System.arraycopy(buff, 11, ud, 0, udLen);
				}
			}
			return ud;
		}

		private int rssiLen = -1;
		private byte[] rssi = null;

		public byte[] getRSSI() {
			if (buff != null && buff.length >= 11) {
				if (udLen == -1) {
					udLen = buff[10];
				}
				if (udLen != -1 && rssiLen == -1 && buff.length >= 12 + udLen) {
					rssiLen = buff[11 + udLen];
				}
				if (rssiLen > 0 && buff.length >= udLen + rssiLen + 12) {
					rssi = new byte[rssiLen];
					System.arraycopy(buff, 12 + udLen, rssi, 0, rssiLen);
				}
			}
			return rssi;
		}

		private int readTimeLen = -1;
		private byte[] readTime = null;

		public byte[] getReadTime() {
			if (buff != null && buff.length > 13) {
				if (udLen == -1) {
					udLen = buff[10];
				}
				if (udLen != -1 && rssiLen == -1 && buff.length >= 12 + udLen) {
					rssiLen = buff[11 + udLen];
				}
				if (rssiLen != -1 && readTimeLen == -1
						&& buff.length >= 13 + udLen + rssiLen)
					readTimeLen = buff[12 + udLen + rssiLen];
				if (readTimeLen > 0
						&& buff.length >= udLen + rssiLen + readTimeLen + 13) {
					// 数据内容
					this.readTime = new byte[readTimeLen];
					System.arraycopy(buff, rssiLen + udLen + 13, readTime, 0,
							readTime.length);
				}
			}
			return this.readTime;
		}
	}
}
