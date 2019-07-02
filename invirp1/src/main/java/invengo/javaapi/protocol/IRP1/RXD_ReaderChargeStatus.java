package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.ReaderChargeStatusReceivedInfo;

/**
 * XC2600-充电状态
 */
public class RXD_ReaderChargeStatus extends BaseMessageNotification {

	public ReaderChargeStatusReceivedInfo getReceivedMessage() {

		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null){
			return null;
		}
		return new ReaderChargeStatusReceivedInfo(data);
	}

}
