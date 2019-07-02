package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class GBAccessReadReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = -7715208647800584366L;
	private boolean isUtcEnable;
	public GBAccessReadReceivedInfo(byte[] buff, boolean isUtcEnable) {
		super(buff);
		this.isUtcEnable = isUtcEnable;
	}
	
	public byte getAntenna(){
		if(null != buff && buff.length > 1){
			return buff[0];
		}
		return 0x00;
	}
	
	private byte[] tagData = null;
	public byte[] getTagData(){
		if(isUtcEnable){//utc(8 bytes)
			if(null != buff && buff.length >= 10){
				tagData = new byte[buff.length - 9];
				System.arraycopy(buff, 1, tagData, 0, tagData.length);
			}
		}else {//no utc
			if(null != buff && buff.length >= 2){
				tagData = new byte[buff.length - 1];
				System.arraycopy(buff, 1, tagData, 0, tagData.length);
			}
		}
		return tagData;
	}
	
	private byte[] utcData = null;
	public byte[] getUTC(){
		if(isUtcEnable){
			if(null != buff && buff.length >= 10){
				utcData = new byte[8];
				System.arraycopy(buff, (buff.length - 8), utcData, 0, 8);
			}
		}
		return utcData;
	}

}
