package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class ReadUserData6CReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = -6829799872953098750L;

	public ReadUserData6CReceivedInfo(byte[] buff) {
		super(buff);
	}

	public byte getAntenna() {
		if (buff != null && buff.length >= 1) {
			return buff[0];
		}
		return 0x00;

	}

	private byte[] userdata = null;

	public byte[] getUserData() {
		if (buff != null && buff.length >= 2) {
			userdata = new byte[buff.length - 1];
			System.arraycopy(buff, 1, userdata, 0, userdata.length);
		}
		return userdata;
	}

}
