package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class ReadUserDataNonFixed6BReceivedInfo extends ReceivedInfo {

	public ReadUserDataNonFixed6BReceivedInfo(byte[] buff) {
		super(buff);
	}

	public byte getAntenna() {
		if (buff != null && buff.length >= 1)
			return buff[0];
		return 0x00;
	}

	private byte[] userdata = null;

	public byte[] getUserData() {
		if (buff != null && buff.length >= 5) {
			userdata = new byte[buff.length - 4];
			System.arraycopy(buff, 4, userdata, 0, userdata.length);
		}
		return userdata;
	}

}
