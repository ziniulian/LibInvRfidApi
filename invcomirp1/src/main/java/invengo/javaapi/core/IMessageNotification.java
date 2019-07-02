package invengo.javaapi.core;


public interface IMessageNotification{

	byte[] getReceivedData();
	void setReceivedData(byte[] receivedData);
	int getMessageID();
	int getStatusCode();
	void setStatusCode(int code);
	String getErrInfo();
	String getMessageType();
	IMessageNotification clone();
}
