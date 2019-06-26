package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.IRP1.BaseMessageNotification;
import invengo.javaapi.protocol.IRP1.Decode;

class RXD_ID_6B extends BaseMessageNotification {

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo{

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

		public byte[] getTID() {
			if (buff != null && buff.length > 4) {
				tid = new byte[buff.length - 4];
				System.arraycopy(buff, 2, tid, 0, tid.length);
			}
			return tid;
		}

		private byte[] rssi = null;

		public byte[] getRssi() {
			if (buff != null && buff.length >= 4) {
				rssi = new byte[2];
				System.arraycopy(buff, buff.length - 2, rssi, 0, 2);
			}
			return rssi;
		}
	}
}
