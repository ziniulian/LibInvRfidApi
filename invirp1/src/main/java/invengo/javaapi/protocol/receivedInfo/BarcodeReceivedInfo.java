package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;
import invengo.javaapi.protocol.IRP1.Reader;

public class BarcodeReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 6853346653775407489L;

	private Reader currentReader;
	public BarcodeReceivedInfo(byte[] buff, Reader currentReader) {
		super(buff);
		this.currentReader = currentReader;
	}
	
	private byte[] barcodeData = null;
	public byte[] getBarcodeData() {
		if(null != currentReader){
			if(currentReader.isUtcEnable()){//utc(8 bytes)
				if (buff != null && buff.length >= 9) {
					barcodeData = new byte[buff.length - 8];
					System.arraycopy(buff, 0, barcodeData, 0, barcodeData.length);
				}
			}else {//no utc
				if(null != buff && buff.length >= 1){
					barcodeData = buff;
				}
			}
		}
		return barcodeData;
	}
	
	private byte[] utc = null;
	public byte[] getUtc(){
		if(currentReader.isUtcEnable()){
			if(buff != null && buff.length >= 9){
				utc = new byte[8];
				System.arraycopy(buff, (buff.length - 8), utc, 0, utc.length);
			}
		}
		return utc;
	}
	
}
