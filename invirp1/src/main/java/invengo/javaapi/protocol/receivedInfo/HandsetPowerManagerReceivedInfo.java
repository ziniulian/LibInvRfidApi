package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class HandsetPowerManagerReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 6508915388481666736L;

	public HandsetPowerManagerReceivedInfo(byte[] buff) {
		super(buff);
	}

	private byte[] powerData = null;
	public byte[] getPower(){
		if(null != buff && buff.length >= 1){
			powerData = new byte[buff.length];
			System.arraycopy(buff, 0, powerData, 0, powerData.length);
		}
		return powerData;
	}
	
}
