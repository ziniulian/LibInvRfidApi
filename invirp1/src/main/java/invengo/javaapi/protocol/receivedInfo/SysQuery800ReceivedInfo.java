package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class SysQuery800ReceivedInfo extends ReceivedInfo {

	public SysQuery800ReceivedInfo(byte[] buff) {
		super(buff);
	}

	public byte[] getQueryData() {
		return buff;
	}

}
