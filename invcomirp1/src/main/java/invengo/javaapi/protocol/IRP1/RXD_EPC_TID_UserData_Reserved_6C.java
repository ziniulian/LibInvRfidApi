package invengo.javaapi.protocol.IRP1;

class RXD_EPC_TID_UserData_Reserved_6C extends BaseMessageNotification {

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

		public byte getAntenna() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;
		}

		private int epcLen = -1;
		private byte[] epc = null;

		public byte[] getEPC() {
			if (buff != null && buff.length >= 3) {
				if (epcLen == -1) {
					epcLen = (buff[1] << 8) + buff[2];
				}
				if (buff.length >= epcLen + 3) {
					// 数据内容
					epc = new byte[epcLen];
					System.arraycopy(buff, 3, epc, 0, epcLen);
				}
			}
			return this.epc;
		}

		private int tidLen = -1;
		private byte[] tid = null;

		public byte[] getTID() {
			if (buff != null && buff.length >= 3) {
				if (epcLen == -1) {
					epcLen = (buff[1] << 8) + buff[2];
				}
				if (tidLen == -1 && buff.length >= 5 + epcLen) {
					tidLen = (buff[3 + epcLen] << 8) + buff[4 + epcLen];
				}
				if (tidLen > 0 && buff.length >= epcLen + tidLen + 5) {
					tid = new byte[tidLen];
					System.arraycopy(buff, 5 + epcLen, tid, 0, tidLen);
				}
			}
			return tid;
		}

		private int reservedLen = -1;
		private byte[] reserved = null;

		public byte[] getReserved() {
			if (buff != null && buff.length >= 3) {
				if (epcLen == -1) {
					epcLen = (buff[1] << 8) + buff[2];
				}
				if (tidLen == -1 && buff.length >= 5 + epcLen) {
					tidLen = (buff[3 + epcLen] << 8) + buff[4 + epcLen];

				}
				if (tidLen != -1 && reservedLen == -1
						&& buff.length >= 7 + epcLen + tidLen) {
					reservedLen = (buff[5 + epcLen + tidLen] << 8)
							+ buff[6 + epcLen + tidLen];
				}
				if (reservedLen > 0
						&& buff.length >= epcLen + tidLen + reservedLen + 7) {
					// 数据内容
					this.reserved = new byte[reservedLen];
					System.arraycopy(buff, 7 + epcLen + tidLen, reserved, 0,
							reservedLen);
				}
			}
			return this.reserved;
		}

		private int udLen = -1;
		private byte[] ud = null;

		public byte[] getUserData() {
			if (buff != null && buff.length >= 3) {
				if (epcLen == -1) {
					epcLen = (buff[1] << 8) + buff[2];

				}
				if (tidLen == -1 && buff.length >= 5 + epcLen) {
					tidLen = (buff[3 + epcLen] << 8) + buff[4 + epcLen];

				}
				if (tidLen != -1 && reservedLen == -1
						&& buff.length >= 7 + epcLen + tidLen) {
					reservedLen = (buff[5 + epcLen + tidLen] << 8)
							+ buff[6 + epcLen + tidLen];
				}
				if (reservedLen != -1 && udLen == -1
						&& buff.length >= 9 + epcLen + tidLen + reservedLen) {
					udLen = (buff[7 + epcLen + tidLen + reservedLen] << 8)
							+ buff[8 + epcLen + tidLen + reservedLen];
				}
				if (udLen > 0
						&& buff.length >= 9 + epcLen + tidLen + reservedLen
						+ udLen) {
					// 数据内容
					this.ud = new byte[udLen];
					System.arraycopy(buff, 9 + epcLen + tidLen + reservedLen,
							ud, 0, udLen);
				}
			}
			return this.ud;
		}

		private int rssiLen = -1;
		private byte[] rssi = null;

		public byte[] getRSSI() {
			if (buff != null && buff.length >= 3) {
				if (epcLen == -1) {
					epcLen = (buff[1] << 8) + buff[2];
				}
				if (tidLen == -1 && buff.length >= 5 + epcLen) {
					tidLen = (buff[3 + epcLen] << 8) + buff[4 + epcLen];
				}
				if (tidLen != -1 && reservedLen == -1
						&& buff.length >= 7 + epcLen + tidLen) {
					reservedLen = (buff[5 + epcLen + tidLen] << 8)
							+ buff[6 + epcLen + tidLen];
				}
				if (reservedLen != -1 && udLen == -1
						&& buff.length >= 9 + epcLen + tidLen + reservedLen) {
					udLen = (buff[7 + epcLen + tidLen + reservedLen] << 8)
							+ buff[8 + epcLen + tidLen + reservedLen];
				}
				if (udLen != -1
						&& rssiLen == -1
						&& buff.length >= 10 + epcLen + tidLen + reservedLen
						+ udLen) {
					rssiLen = buff[9 + epcLen + tidLen + reservedLen + udLen];
				}
				if (rssiLen > 0
						&& buff.length >= 10 + epcLen + tidLen + reservedLen
						+ udLen + rssiLen) {
					rssi = new byte[rssiLen];
					System.arraycopy(buff, 10 + epcLen + tidLen + reservedLen
							+ udLen, rssi, 0, rssiLen);
				}
			}
			return rssi;
		}

		private int readTimeLen = -1;
		private byte[] readTime = null;

		public byte[] getReadTime() {
			if (buff != null && buff.length >= 3) {
				if (epcLen == -1) {
					epcLen = (buff[1] << 8) + buff[2];
				}
				if (tidLen == -1 && buff.length >= 5 + epcLen) {
					tidLen = (buff[3 + epcLen] << 8) + buff[4 + epcLen];
				}
				if (tidLen != -1 && reservedLen == -1
						&& buff.length >= 7 + epcLen + tidLen) {
					reservedLen = (buff[5 + epcLen + tidLen] << 8)
							+ buff[6 + epcLen + tidLen];
				}
				if (reservedLen != -1 && udLen == -1
						&& buff.length >= 9 + epcLen + tidLen + reservedLen) {
					udLen = (buff[7 + epcLen + tidLen + reservedLen] << 8)
							+ buff[8 + epcLen + tidLen + reservedLen];
				}
				if (udLen != -1
						&& rssiLen == -1
						&& buff.length >= 10 + epcLen + tidLen + reservedLen
						+ udLen) {
					rssiLen = buff[9 + epcLen + tidLen + reservedLen + udLen];
				}
				if (rssiLen != -1
						&& readTimeLen == -1
						&& buff.length >= 11 + epcLen + tidLen + reservedLen
						+ udLen + rssiLen) {
					readTimeLen = buff[10 + epcLen + tidLen + reservedLen
							+ udLen + rssiLen];
				}
				if (readTimeLen > 0
						&& buff.length >= 11 + epcLen + tidLen + reservedLen
						+ udLen + rssiLen + readTimeLen) {
					// 数据内容
					this.readTime = new byte[readTimeLen];
					System.arraycopy(buff, 11 + epcLen + tidLen + reservedLen
							+ udLen + rssiLen, readTime, 0, readTime.length);
				}
			}
			return this.readTime;
		}
	}
}
