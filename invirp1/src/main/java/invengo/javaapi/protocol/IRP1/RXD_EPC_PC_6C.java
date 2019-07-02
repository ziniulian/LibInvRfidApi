package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.IRP1.BaseMessageNotification;
import invengo.javaapi.protocol.IRP1.Decode;

class RXD_EPC_PC_6C extends BaseMessageNotification {

	public ReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data==null) {
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

		private byte[] pc = null;

		public byte[] getPc() {
			if (buff != null & buff.length >= 3) {
				pc = new byte[2];
				System.arraycopy(buff, 1, pc, 0, 2);
			}
			return pc;
		}

		private byte[] epc = null;

		public byte[] getEPC() {
			if (buff != null && buff.length > 5) {
				epc = new byte[buff.length - 5];
				System.arraycopy(buff, 3, epc, 0, epc.length);
			}
			return epc;
		}

		private byte[] rssi = null;

		public byte[] getRssi() {
			if (buff != null && buff.length >= 3) {
				rssi = new byte[2];
				System.arraycopy(buff, buff.length - 2, rssi, 0, 2);
			}
			return rssi;
		}
	}
}
