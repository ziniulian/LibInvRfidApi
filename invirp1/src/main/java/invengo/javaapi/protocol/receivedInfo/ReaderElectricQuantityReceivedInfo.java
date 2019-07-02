package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class ReaderElectricQuantityReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 3033765607240188799L;

	public ReaderElectricQuantityReceivedInfo(byte[] buff) {
		super(buff);
	}

	private int percent = 0;
	public int getElectricQuantityPercent(){
		if (buff != null && buff.length >= 1) {
			percent = buff[0] & 0xFF;
		}
		return percent;
	}
	
}
