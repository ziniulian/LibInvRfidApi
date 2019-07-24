package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class QueryTagPasswordReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = -1354422319723343405L;

	public QueryTagPasswordReceivedInfo(byte[] buff) {
		super(buff);
	}
	
	public byte getAntenna() {
		if (buff != null && buff.length >= 1) {
			return buff[0];
		}
		return 0x00;
	}

	private byte[] userdata = null;

	public byte[] getTagPassword() {
		if (buff != null && buff.length >= 3) {
			userdata = new byte[2];
			System.arraycopy(buff, 1, userdata, 0, userdata.length);
		}
		return userdata;
	}

}
