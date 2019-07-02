package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.HandsetTriggerManagerReceivedInfo;

/**
 * 手持机主动上报之扳机状态
 */
public class HandsetTriggerManager extends BaseMessageNotification {

	public HandsetTriggerManagerReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(null == data){
			return null;
		}
		return new HandsetTriggerManagerReceivedInfo(data);
	}

}
