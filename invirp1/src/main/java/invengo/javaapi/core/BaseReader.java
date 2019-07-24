package invengo.javaapi.core;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.invengo.lib.diagnostics.InvengoLog;

import android.app.Activity;
import invengo.javaapi.communication.Bluetooth;
import invengo.javaapi.core.Util.LogType;
import invengo.javaapi.handle.IApiExceptionHandle;
import invengo.javaapi.handle.IBLEConnectionStateChangeHandle;
import invengo.javaapi.handle.IBuffReceivedHandle;
import invengo.javaapi.handle.IBufferReceivedHandle;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.handle.IMsgReceivedHandle;
import invengo.javaapi.protocol.IRP1.SysConfig_800;

public abstract class BaseReader implements IMsgReceivedHandle, IApiExceptionHandle,
		Serializable, IBuffReceivedHandle, IBLEConnectionStateChangeHandle{

	private static final long serialVersionUID = -96260458233557377L;
	private static final String TAG = BaseReader.class.getSimpleName();
	protected ICommunication iComm;
	public String modelNumber = "unknown";
	public String readerName = "Reader1";
	public String readerGroup = "Group1";
	public String portType = "RS232";
	public String protocolVersion = "IRP2";
	public String connStr;
	public Activity context;
	public ReaderChannelType channelType;
	
	public static final String ACTION_READER_CONNECTED = "invengo.javaapi.core.BaseReader.ACTION_READER_CONNECTED";
	public static final String ACTION_READER_DISCONNECTED = "invengo.javaapi.core.BaseReader.ACTION_READER_DISCONNECTED";
	private boolean isExistReaderConfig = true;

	private Socket server;
	
	private static final byte PARAMETER_RFID_1D2D = (byte) 0x84;
	private static final byte[] RFID_DATA = new byte[]{0x01, 0x00};
	private static final byte[] BARCODE_DATA = new byte[]{0x01, 0x01};
	private static final byte PARAMETER_VERIFY_TIME = 0x10;
	
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

	public ReaderChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ReaderChannelType channelType) {
		this.channelType = channelType;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
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

	public BaseReader(Socket server, String protocolVersion) {
		InetAddress ip = server.getInetAddress();
		int port = server.getPort();
		this.readerName = ip.getHostAddress() + ":" + String.valueOf(port);
		this.readerGroup = String.valueOf(server.getLocalPort());
		this.server = server;
		this.protocolVersion = protocolVersion;
		this.portType = "TCPIP_Server";
	}
	
	/**
	 * BLE
	 */
	public BaseReader(String readerName, String protocolVersion, String portType,
			String connStr, Activity context) {
		this.readerName = readerName;
		this.protocolVersion = protocolVersion;
		this.portType = portType;
		this.connStr = connStr;
		this.context = context;
	}

	public boolean connect() {
		if (!isExistReaderConfig) {
			Util.logAndTriggerApiErr(readerName, "FF18", readerName, LogType.Error);
			return false;
		}
		String connClassName = this.portType;
		try {
			if(null != this.context){//BLE
				iComm = CommunicationFactory.createCommunication(connClassName, this.context);
			}else{
				if (server != null) {
					iComm = CommunicationFactory.createCommunication(connClassName, server);
				} else {
					iComm = CommunicationFactory.createCommunication(connClassName);
				}
			}
		} catch (Exception e) {
			Util.logAndTriggerApiErr(readerName, "FF12", e.getMessage(), LogType.Fatal);
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
				boolean opened = iComm.open(connStr);
//				InvengoLog.e(TAG, "ERROR. serial port open {%s}", opened);
				if(null == this.context){
					if (opened) {
						return doAfterActuallyConnect();
					}
				}else {//BLE
					return opened;
				}
			} catch (Exception e) {
				Util.logAndTriggerApiErr(readerName, "FF19", readerName + ":" + e.getMessage(), LogType.Error);
				return false;
			}
		}
		return false;
	}

	protected boolean doAfterActuallyConnect() {
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
		
		if(iComm instanceof Bluetooth){//bluetooth--XC2600
			//校时
			verifyRfidTime();
			if(null == getChannelType()){
				setChannelType(ReaderChannelType.RFID_CHANNEL_TYPE);
			}
			if(!attemptChannelSelect(getChannelType())){
				return false;
			}
//			if(this.channelType == ReaderChannelType.RFID_CHANNEL_TYPE){//条码通道不需要发送io状态查询指令
//				if (iComm.iProcess.getConnectMessage() != null) {
//					boolean isConn = false;
//					for (int i = 0; i < 3;) {
//						IMessage im = iComm.iProcess.getConnectMessage();
//						send(im, 500);
//						if (im.getStatusCode() != 0xff){
//							isConn = true;
//						}// 不超时
//						break;
//					}
////					InvengoLog.w(TAG, "Warn. Timeout.");
//					if (!isConn) {
//						disConnect();
//						return false;
//					}
//				}
//			}
		}else if(null != this.context) {//BLE--手环式读写器
			//
		}else{
			if (iComm.iProcess.getConnectMessage() != null) {
				boolean isConn = false;
				for (int i = 0; i < 3;) {
					IMessage im = iComm.iProcess.getConnectMessage();
					send(im, 500);
//					InvengoLog.e(TAG, "Error. statusCode {%s}", im.getStatusCode());
					if (im.getStatusCode() != 0xff){
						isConn = true;
						break;
					}// 不超时
				}
				if (!isConn) {
					InvengoLog.e(TAG, "Warn. Timeout.");
					disConnect();
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean verifyRfidTime() {
		long time = System.currentTimeMillis();
		long second = time / 1000;
		long mircosecond = (time % 1000) * 1000;
		byte[] data = new byte[9];
		data[0] = 8;
		data[1] = (byte) (second >> 24);
		data[2] = (byte) (second >> 16);
		data[3] = (byte) (second >> 8);
		data[4] = (byte) (second & 0xFF);
		
		data[5] = (byte) (mircosecond >> 24);
		data[6] = (byte) (mircosecond >> 16);
		data[7] = (byte) (mircosecond >> 8);
		data[8] = (byte) (mircosecond & 0xFF);
		
		SysConfig_800 message = new SysConfig_800(PARAMETER_VERIFY_TIME, data);
		return send(message);
	}

	private boolean attemptChannelSelect(ReaderChannelType channelType){
		SysConfig_800 message = null;
		if (channelType == ReaderChannelType.RFID_CHANNEL_TYPE) {
			message = new SysConfig_800(PARAMETER_RFID_1D2D, RFID_DATA);
		} else if(channelType == ReaderChannelType.BARCODE_CHANNEL_TYPE){
			message = new SysConfig_800(PARAMETER_RFID_1D2D, BARCODE_DATA);
		}
		boolean success = send(message);

		return success;
	}


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
			if (iComm.threadProcess != null && iComm.threadProcess.isAlive()) {
				iComm.threadProcess = null;
			}
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
		if(null == iComm){
			return false;
		}
		try {
			msg.triggerOnExecuting(this);
		} catch (Exception e) {
			Util.logAndTriggerApiErr(readerName, "FF26", e.getMessage(), LogType.Debug);
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
	
	@Override
	public void onConnectionStateChange() {
		
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
	
	//XC-IUT1501-01芯片测温标签初始值及步进值
	private int initValue = 30;
	private int stepValue = 20;

	public int getInitValue() {
		return initValue;
	}

	public void setInitValue(int initValue) {
		this.initValue = initValue;
	}

	public int getStepValue() {
		return stepValue;
	}

	public void setStepValue(int stepValue) {
		this.stepValue = stepValue;
	}
	
	// Check RFID Module
	public abstract boolean check();
	
	public enum ReaderChannelType{
		RFID_CHANNEL_TYPE, BARCODE_CHANNEL_TYPE
	}
}