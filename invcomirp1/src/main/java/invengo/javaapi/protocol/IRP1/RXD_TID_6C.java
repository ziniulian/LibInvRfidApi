package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.IRP1.BaseMessageNotification;
import invengo.javaapi.protocol.IRP1.Decode;

class RXD_TID_6C extends BaseMessageNotification {

	public ReceivedInfo getReceivedMessage(){
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

		private byte[] tid = null;

		public byte[] getTID() {
			if (buff != null && buff.length > 1) {
				tid = new byte[buff.length - 1];
				System.arraycopy(buff, 1, tid, 0, tid.length);
			}
			return tid;
		}
	}
}
