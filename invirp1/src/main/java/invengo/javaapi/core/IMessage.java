package invengo.javaapi.core;

public interface IMessage extends IMessageNotification {

	boolean getIsReturn();

	void setIsReturn(boolean isReturn);

	int getTimeOut();

	void setTimeOut(int timeOut);

	byte[] getTransmitterData();

	void setTransmitterData(byte[] transmitterData);

	String getPortType();

	void setPortType(String portType);

	void triggerOnExecuting(Object obj);

	void triggerOnExecuted(Object obj);

	byte[] fromXML(String xmlString);

}
