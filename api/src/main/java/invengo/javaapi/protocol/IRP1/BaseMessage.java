package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.ErrorInfo;
import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.ReceivedInfo;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

public abstract class BaseMessage extends MessageFrame implements IMessage {

	protected boolean isReturn = true;
	protected int timeout = 1000;
	protected byte[] rxData = null;
	protected int msgID;
	protected int statusCode = -1;
	protected byte[] txData = null;
	protected String portType;
	protected String errInfo;
	protected List<IEventHandle> onExecuting = new ArrayList<IEventHandle>();
	protected List<IEventHandle> onExecuted = new ArrayList<IEventHandle>();
	protected BaseMessage msg = this;

	protected byte[] tagID;
	protected MemoryBank tagIDType;

	public BaseMessage() {
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

	public boolean getIsReturn() {
		return this.isReturn;
	}

	public void setIsReturn(boolean isReturn) {
		this.isReturn = isReturn;
	}

	public int getStatusCode() {
		if (this.statusCode == -1) {
			if (rxData != null && rxData.length >= 6) {
				if (msgType > 256) {
					this.statusCode = rxData[4];
				} else {
					this.statusCode = rxData[3];
				}
			}
		}
		return this.statusCode;
	}

	public void setStatusCode(int value) {
		this.statusCode = value;
	}

	public int getTimeOut() {
		return this.timeout;
	}

	public void setTimeOut(int timeOut) {
		this.timeout = timeOut;
	}

	public byte[] getTransmitterData() {
		msgID = super.msgType;
		int len = 1;
		if (super.msgType > 256) {
			len++;
		}
		if (super.msgBody != null) {
			len = len + super.msgBody.length;
		}
		msgLen[0] = (byte) (len >> 8);
		msgLen[1] = (byte) (len & 0xff);
		byte[] data = new byte[len + 2];
		int p = 0;
		System.arraycopy(msgLen, 0, data, p, 2);
		p += 2;
		if (super.msgType > 256) {
			data[p] = (byte) (msgType >> 8);
			data[p + 1] = (byte) (msgType & 0xff);
			p += 2;
		} else {
			data[p++] = (byte) msgType;
		}
		if (msgBody != null) {
			System.arraycopy(msgBody, 0, data, p, msgBody.length);
			p += 2;
		}
		super.crc = CRCClass.getCRC16(data);
		txData = new byte[len + 4];
		System.arraycopy(data, 0, txData, 0, len + 2);
		System.arraycopy(crc, 0, txData, len + 2, 2);
		if (this.portType.equals("RS232") || this.portType.equals("USB")) {
			txData = Decode.formatData(txData);
		}

		return this.txData;
	}

	public void setTransmitterData(byte[] transmitterData) {
		this.txData = transmitterData;
	}

	public String getPortType() {
		return this.portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public void triggerOnExecuted(Object obj) {
		if (onExecuted != null) {
			for (int i = 0; i < onExecuted.size(); i++) {
				onExecuted.get(i).eventHandle_executed(obj, new EventArgs());
			}
		}
	}

	public void triggerOnExecuting(Object obj) {
		if (onExecuting != null) {
			for (int i = 0; i < onExecuting.size(); i++) {
				onExecuting.get(i).eventHandle_executing(obj, new EventArgs());
			}
		}
	}

	class sendHelp {
		Object obj;
		BaseMessage msg;

		public sendHelp(Object obj, BaseMessage msg) {
			this.obj = obj;
			this.msg = msg;
		}

		public void send() {
			if (msg.onExecuted != null)
				for (int i = 0; i < onExecuted.size(); i++) {
					onExecuted.get(i).eventHandle_executed(obj, new EventArgs());
				}
		}
	}

	public int getMessageID() {
		if (msgID == 0) {
			this.msgID = super.msgType;
		}
		return this.msgID;
	}

	public String getMessageType() {
		return this.getClass().getSimpleName();
	}

	public byte[] getReceivedData() {
		return this.rxData;
	}

	public void setReceivedData(byte[] receivedData) {
		this.rxData = receivedData;
	}

	public String getErrInfo() {
		if (statusCode != 0) {
			String key = String.format("%1$02X", this.statusCode);
			if (ErrorInfo.errMap.containsKey(key)) {
				errInfo = ErrorInfo.errMap.get(key);
			} else {
				errInfo = key;
			}
		}
		return errInfo;
	}

	public void messageNotificationReceivedHandle(BaseReader reader,
			IMessageNotification msg) {

	}

	public byte[] fromXML(String xmlString) {
		return null;
	}

	public String toXML() {
		return "";
	}

	public ReceivedInfo getReceivedMessage() {
		return null;
	}

	protected void selectTag(Object sender, EventArgs e) {
		BaseReader reader = (BaseReader) sender;
		if (tagID != null) {
			int sc = SelectTag_6C.select(reader, tagID, tagIDType);
			if (sc != 0) {
				this.statusCode = sc;
				String key = String.format("%1$02X", getStatusCode());
				if (ErrorInfo.errMap.containsKey(key)) {
					this.errInfo = ErrorInfo.errMap.get(key);
				}else {
					errInfo = key;
				}
				throw new RuntimeException(this.errInfo);
			}
		}
	}

	public IMessageNotification clone() {
		this.statusCode = -1;
		this.rxData = null;
		return this;
	}
}
