package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.BarcodeReceivedInfo;

public class RXD_BARCODE extends BaseMessageNotification {

	public BarcodeReceivedInfo getReceivedMessage() {

		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null){
			return null;
		}
		return new BarcodeReceivedInfo(data, this.currentReader);
	}

	private Reader currentReader;
	public void setCurrentReader(Reader currentReader) {
		this.currentReader = currentReader;
	}

}
