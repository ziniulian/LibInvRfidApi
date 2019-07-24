package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class SysQuery800ReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 5604672299197974115L;

	public SysQuery800ReceivedInfo(byte[] buff) {
		super(buff);
	}

	public byte[] getQueryData() {
		return buff;
	}

}
