package invengo.javaapi.core;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.Util.LogType;
import invengo.javaapi.handle.IApiExceptionHandle;
import invengo.javaapi.handle.IBuffReceivedHandle;
import invengo.javaapi.handle.IBufferReceivedHandle;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.handle.IMsgReceivedHandle;

public abstract class BaseReader implements IMsgReceivedHandle, IApiExceptionHandle,
		Serializable, IBuffReceivedHandle {

	/**
	 *
	 */
	private static final long serialVersionUID = -96260458233557377L;

	protected ICommunication iComm;
	public String modelNumber = "unknown";
	public String readerName = "Reader1";
	public String readerGroup = "Group1";
	public String portType = "RS232";
	public String protocolVersion = "IRP2";
	public String connStr;
	private boolean isExistReaderConfig = true;

	private Socket server;

	public boolean isConnected() {
		boolean isConn = false;
		if (iComm!=null) {
			isConn = iComm.isConnected();
		}
		return isConn;
	}

	public void setCommConnect(boolean connect) {
		iComm.setConnected(connect);
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public String getReaderName() {
		return readerName;
	}

	public String getReaderGroup() {
		return readerGroup;
	}

	public void setReaderGroup(String group) {
		this.readerGroup = group;
	}

	public List<IMessageNotificationReceivedHandle> onMessageNotificationReceived = new ArrayList<IMessageNotificationReceivedHandle>();

	void iConn_OnMsgReceived(IMessageNotification e) {
		if (onMessageNotificationReceived != null) {
			for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
				onMessageNotificationReceived.get(i)
						.messageNotificationReceivedHandle(this, e);
			}
		}
	}

	public static List<IApiExceptionHandle> onApiException = new ArrayList<IApiExceptionHandle>();

	protected static void triggerApiException(String e) {
		if (onApiException != null)
			for (int i = 0; i < onApiException.size(); i++) {
				onApiException.get(i).apiExceptionHandle(e);
			}
	}

	public static List<IBufferReceivedHandle> onBufferReceived;

	void iConn_OnBuffReceived(byte[] e) {
		if (onBufferReceived != null) {
			for (int i = 0; i < onBufferReceived.size(); i++) {
				onBufferReceived.get(i).bufferReceived(this, e);
			}
		}
	}

	public BaseReader(String readerName, String protocolVersion, String portType,
					  String connStr) {
		this.readerName = readerName;
		this.protocolVersion = protocolVersion;
		this.portType = portType;
		this.connStr = connStr;
	}

	public BaseReader(String readerName) {
		this.readerName = readerName;
		ConfigFile cf = new ConfigFile();
		ConfigFileItem cfi = cf.findReaderItem(this.readerName);
		if (cfi != null) {
			this.protocolVersion = cfi.getProtocol();
			this.portType = cfi.getType();
			this.connStr = cfi.getConnStr();
			this.readerGroup = cfi.getReaderGroup();
		} else {
			isExistReaderConfig = false;
		}
	}

	public BaseReader(Socket server, String protocolVersion) {
		InetAddress ip = server.getInetAddress();
		int port = server.getPort();
		this.readerName = ip.getHostAddress() + ":" + String.valueOf(port);
		this.readerGroup = String.valueOf(server.getLocalPort());
		this.server = server;
		this.protocolVersion = protocolVersion;
		this.portType = "TCPIP_Server";
	}

	public boolean connect() {
		if (!isExistReaderConfig) {
			Util.logAndTriggerApiErr(readerName, "FF18", readerName,
					LogType.Error);
			return false;
		}
		String connClassName = this.portType;
		try {
			if (server != null) {
				iComm = CommunicationFactory.createCommunication(connClassName,
						server);
			} else {
				iComm = CommunicationFactory.createCommunication(connClassName);
			}
		} catch (Exception e) {
			Util.logAndTriggerApiErr(readerName, "FF12", e.getMessage(),
					LogType.Fatal);
			return false;
		}
		try {
			iComm.iProcess = (IProcess) Class.forName(
					"invengo.javaapi.protocol." + this.protocolVersion
							+ ".Decode").newInstance();
			iComm.iProcess.setPortType(this.portType);
			iComm.setReader(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (iComm != null) {
			iComm.setReaderName(this.readerName);
			try {
				if (connStr == null) {
					connStr = "";
				}
				if (iComm.open(connStr)) {
					iComm.OnMsgReceived.add(this);
					iComm.onBuffReceived.add(this);
					iComm.threadProcess = new Thread() {
						public void run() {
							iComm.process();
						}
					};
					iComm.threadProcess.start();
					if (server != null) {
						iComm.setConnected(true);
						return true;
					}
					if (iComm.iProcess.getConnectMessage() != null) {
						boolean isConn = false;
						for (int i = 0; i < 3;) {
							IMessage im = iComm.iProcess.getConnectMessage();
							send(im, 300);
							if (im.getStatusCode() != 0xff){
								isConn = true;
							}// 不超时
							break;
						}
						if (!isConn) {
							disConnect();
							return false;
						}
					}
					return true;
				}
			} catch (Exception e) {
				Util.logAndTriggerApiErr(readerName, "FF19", readerName + ":"
						+ e.getMessage(), LogType.Error);
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void disConnect() {
		if (iComm != null && iComm.iProcess.getDisconnectMessage() != null) {
			iComm.setConnected(false);
			IMessage im = iComm.iProcess.getDisconnectMessage();
			send(im,250);
		}
		if (iComm != null) {
			iComm.close();
			iComm.OnMsgReceived.remove(this);
			iComm.onBuffReceived.remove(this);
		}
		if (iComm.threadProcess != null && iComm.threadProcess.isAlive()) {
			iComm.threadProcess.stop();
		}
		iComm = null;
	}

	public boolean send(byte[] msg) {
		int sCount = iComm.send(msg);
		if (sCount == msg.length) {
			return true;
		}
		return false;
	}

	public boolean send(IMessage msg) {
		try {
			msg.triggerOnExecuting(this);
		} catch (Exception e) {
			Util.logAndTriggerApiErr(readerName, "FF26", e.getMessage(),
					LogType.Debug);
			return false;
		}
		boolean isSuc = iComm.send(msg, msg.getTimeOut());
		msg.triggerOnExecuted(this);
		return isSuc;
	}

	public boolean send(IMessage msg, int timeout) {
		msg.setTimeOut(timeout);
		return send(msg);
	}

	public String getPortType() {
		return portType;
	}

	public void bufferReceivedHandle(IMessageNotification e) {
		iConn_OnMsgReceived(e);
	}

	public void apiExceptionHandle(String e) {
		triggerApiException(e);
	}

	public void bufferReceived(byte[] e) {
		iConn_OnBuffReceived(e);
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

}