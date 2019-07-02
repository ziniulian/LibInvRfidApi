package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class TagOperationQuery6CReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 4021111012160581068L;

	public TagOperationQuery6CReceivedInfo(byte[] buff) {
		super(buff);
	}

	public byte[] getQueryData() {
		return buff;
	}
	
}
