package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.IRP1.BaseMessageNotification;
import invengo.javaapi.protocol.IRP1.Decode;

class RXD_EPC_6C extends BaseMessageNotification {

	public ReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data==null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo{
		
		public ReceivedInfo(byte[] buff){
			super(buff);
		}
		
		public byte getAntenna() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;
		}
		
		private byte[] epc = null;
		
		public byte[] getEPC() {
			if (buff != null && buff.length >= 2) {
				epc = new byte[buff.length - 1];
				System.arraycopy(buff, 1, epc, 0, epc.length);
			}
			return epc;
		}
	}

}