package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class HandsetTriggerManagerReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = -7400032834712779006L;
	public HandsetTriggerManagerReceivedInfo(byte[] buff) {
		super(buff);
	}
	
	private byte[] status = null;
	public byte[] getStatus(){
		if(null != buff & buff.length >= 1){
			status = new byte[buff.length];
			System.arraycopy(buff, 0, status, 0, status.length);
		}
		return status;
	}

}
