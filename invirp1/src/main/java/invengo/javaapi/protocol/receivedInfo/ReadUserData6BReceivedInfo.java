package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class ReadUserData6BReceivedInfo extends ReceivedInfo {

	public ReadUserData6BReceivedInfo(byte[] buff) {
		super(buff);
	}

	public byte getAntenna() {
		if (buff != null && buff.length >= 1){
			return buff[0];
		}
		return 0x00;

	}

	private byte[] userdata = null;

	public byte[] getUserData() {
		if (buff != null && buff.length >= 4) {
			userdata = new byte[buff.length - 3];
			System.arraycopy(buff, 3, userdata, 0, userdata.length);
		}
		return userdata;
	}
	
}
