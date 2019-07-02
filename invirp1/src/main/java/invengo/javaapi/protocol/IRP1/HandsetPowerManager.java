package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.HandsetPowerManagerReceivedInfo;

/**
 * 手持机主动上报之电源信息
 */
public class HandsetPowerManager extends BaseMessageNotification {

	public HandsetPowerManagerReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(null == data){
			return null;
		}
		return new HandsetPowerManagerReceivedInfo(data);
	}

}
