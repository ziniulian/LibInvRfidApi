package invengo.javaapi.core;

import java.util.List;

public interface IProcess {

	String getPortType();
	void setPortType(String portType);
	IMessage getConnectMessage();
	IMessage getDisconnectMessage();
	void parse(byte[] buff,List<byte[]> msgs);
	IMessageNotification parseMessageNoticefaction(byte[] recvMsg);
	int getMessageID(byte[] msg);
}
