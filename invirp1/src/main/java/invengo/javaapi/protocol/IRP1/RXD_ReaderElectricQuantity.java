package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.ReaderElectricQuantityReceivedInfo;

/**
 * XC2600-电量
 */
public class RXD_ReaderElectricQuantity extends BaseMessageNotification {

	public ReaderElectricQuantityReceivedInfo getReceivedMessage() {

		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null){
			return null;
		}
		return new ReaderElectricQuantityReceivedInfo(data);
	}

}
