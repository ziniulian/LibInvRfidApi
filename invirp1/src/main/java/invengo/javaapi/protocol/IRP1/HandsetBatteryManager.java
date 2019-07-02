package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.HandsetBatteryManagerReceivedInfo;

/**
 * 手持机主动上报之充电状态
 */
public class HandsetBatteryManager extends BaseMessageNotification {

	public HandsetBatteryManagerReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(null == data){
			return null;
		}
		return new HandsetBatteryManagerReceivedInfo(data);
	}

}
