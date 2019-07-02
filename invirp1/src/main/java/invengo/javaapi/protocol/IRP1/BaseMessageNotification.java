package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.ReceivedInfo;
import invengo.javaapi.core.Util;

public abstract class BaseMessageNotification extends MessageFrame implements
		IMessageNotification {

	protected byte[] rxData = null;
	protected int statusCode = -1;
	protected String errInfo;
	protected int msgID;

	public BaseMessageNotification() {
		String msgType = getMessageType();
		if (MessageType.msgClass.containsKey(msgType)) {
			super.msgType = MessageType.msgClass.get(msgType);
			if (super.msgType > 255) {
				if (super.msgType / 256 != 0x09) {
					super.msgType = super.msgType / 256;
				}
			}
		}
	}

	public int getMessageID() {
		return this.msgID;
	}

	public void setMessageID(int messageID) {
		this.msgID = messageID;
	}

	public String getMessageType() {
		return this.getClass().getSimpleName();
	}

	public byte[] getReceivedData() {
		return this.rxData;
	}

	public int getStatusCode() {
		if (this.statusCode == -1) {
			if (rxData != null && rxData.length >= 6) {
				this.statusCode = rxData[3];
				if (msgType > 256) {
					this.statusCode = rxData[4];
				}
			}
		}
		return this.statusCode;
	}

	public void setStatusCode(int value) {
		this.statusCode = value;
	}

	public void setReceivedData(byte[] receivedData) {
		this.rxData = receivedData;
	}

	public String getErrInfo() {
		if (statusCode != 0) {
			String key = String.format("%1$02X", this.statusCode);
			if (Util.getErrorInfo(key)!=null) {
				errInfo = Util.getErrorInfo(key);
			} else {
				errInfo = key;
			}
		}
		return this.errInfo;
	}

	public ReceivedInfo getReceivedMessage() {
		return null;
	}

	public IMessageNotification clone() {
		this.statusCode = -1;
		this.rxData = null;
		return this;
	}

}
