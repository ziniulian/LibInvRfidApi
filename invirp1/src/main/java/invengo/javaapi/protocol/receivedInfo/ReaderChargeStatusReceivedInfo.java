package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class ReaderChargeStatusReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 2940002534979411867L;

	public ReaderChargeStatusReceivedInfo(byte[] buff) {
		super(buff);
	}
	
	private int status = -1;
	public int getChargeStatus(){
		if (buff != null && buff.length >= 1) {
			status = buff[0] & 0xFF;
		}
		return status;
	}
	
}
