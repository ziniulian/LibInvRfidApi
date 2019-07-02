package invengo.javaapi.protocol.IRP1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.invengo.lib.diagnostics.InvengoLog;
import com.invengo.lib.system.ModuleControl;
import com.invengo.lib.system.device.DeviceManager;
import com.invengo.lib.system.device.type.DeviceType;
import com.invengo.lib.util.SysUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.communication.BluetoothLET;
import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.Util;
import invengo.javaapi.core.Util.LogType;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;

/**
 * 适用设备类型为：XC2600
 */
public class Reader extends BaseReader implements
		IMessageNotificationReceivedHandle {

	private static final long serialVersionUID = -5493947669141168755L;
	private static final String TAG = Reader.class.getSimpleName();
	protected boolean isUtcEnable = false;
	protected boolean isRssiEnable = false;
	protected String readerType = "";
	protected volatile boolean isStopReadTag = false;
	private static int mWakeupCount = 0;

	public List<IMessageNotificationReceivedHandle> onMessageNotificationReceived = new ArrayList<IMessageNotificationReceivedHandle>();

	public Reader(String readerName, String portType, String connStr) {
		super(readerName, "IRP1", portType, connStr);
		super.onMessageNotificationReceived.add(this);
	}

	public Reader(Socket server) {
		super(server, "IRP1");
		super.onMessageNotificationReceived.add(this);
	}

	public boolean isUtcEnable() {
		return isUtcEnable;
	}

	public boolean isRssiEnable(){
		return isRssiEnable;
	}

	/**
	 * BLE
	 */
	public Reader(String readerName, String portType, String connStr, Activity context, ReaderChannelType channelType) {
		super(readerName, "IRP1", portType, connStr, context, channelType);
		super.onMessageNotificationReceived.add(this);
		registerBluetoothBroadcastReceiver();
	}

	public void messageNotificationReceivedHandle(BaseReader reader,
												  IMessageNotification msg) {
		if (msg.getStatusCode() != 0) {
			if (onMessageNotificationReceived != null) {
				for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
					onMessageNotificationReceived.get(i)
							.messageNotificationReceivedHandle(reader, msg);
				}
			}
			return;
		}
		RXD_TagData rxdMsg = new RXD_TagData((Reader) reader, msg);
		if (!rxdMsg.getReceivedMessage().getTagType().equals("")) {
			synchronized (lockinfo) {
				if (this.info != null) {
					if (this.info.isGetOneTag && !this.info.isDone) {
						info.msg.setReceivedData(msg.getReceivedData());
						this.info.isDone = true;
						synchronized (info.getEv()) {
							info.getEv().notify();
						}
					} else {
						int len = msg.getReceivedData().length;
						if (info.msg.getReceivedData() != null) {
							if (info.msg.getReceivedData().length > 0) {
								len += info.msg.getReceivedData().length;
								byte[] data = new byte[len];
								System.arraycopy(info.msg.getReceivedData(), 0,
										data, 0,
										info.msg.getReceivedData().length);
								System.arraycopy(msg.getReceivedData(), 0,
										data,
										info.msg.getReceivedData().length, msg
												.getReceivedData().length);
								info.msg.setReceivedData(data);

							}
						} else
							info.msg.setReceivedData(msg.getReceivedData());
					}
					return;
				}
			}
			if (onMessageNotificationReceived != null) {
				for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
					onMessageNotificationReceived.get(i)
							.messageNotificationReceivedHandle((Reader) reader,
									rxdMsg);
				}
			}
		} else {
			//UTC(时间戳)处理
			if(msg instanceof RXD_BARCODE){
				((RXD_BARCODE)msg).setCurrentReader(this);
			}else if(msg instanceof GBInventoryTag){
				((GBInventoryTag)msg).setCurrentReader(this);
			}else if(msg instanceof GBReadAllBank){
				((GBReadAllBank)msg).setCurrentReader(this);
			}else if(msg instanceof GBAccessReadTag){
				((GBAccessReadTag)msg).setCurrentReader(this);
			}else if(msg instanceof GBCombinationReadTag){
				((GBCombinationReadTag)msg).setCurrentReader(this);
			}


			if (onMessageNotificationReceived != null) {
				for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
					onMessageNotificationReceived.get(i)
							.messageNotificationReceivedHandle((Reader) reader,
									msg);
				}
			}
		}
	}

	public boolean connect() {
		if(isConnected()){
			InvengoLog.i(TAG, "INFO. connect() - Already connected");
			return true;
		}

		// Device Power On
		powerControl(true);

		boolean isConn = super.connect();
		if(null == super.context){
			if (isConn) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				doRssiUtcQuery();
			}else{
				InvengoLog.e(TAG, "ERROR. connect() - Faield to connect device");
				disConnect();
				isConn = false;
			}
		}
		return isConn;
	}

	private void doRssiUtcQuery() {
		readerType = "800";
		// 查询RSSI功能
		if (readerType.equals("800")
				&& super.getModelNumber() != "XC-RF812"
				&& super.getModelNumber() != "XC-RF853") {
			SysQuery_800 msg = new SysQuery_800((byte) 0x14);
			if (super.send(msg, 200)) {
				if (msg.getReceivedMessage().getQueryData() != null
						&& msg.getReceivedMessage().getQueryData().length > 0) {
					if (msg.getReceivedMessage().getQueryData()[0] == 0x01) {

						isRssiEnable = true;
					} else {
						isRssiEnable = false;
					}
				}
			} else {
				isRssiEnable = false;
			}
			InvengoLog.i(TAG, "INFO.RSSI Enable {%s}", String.valueOf(isRssiEnable));
		} else if (super.modelNumber.toUpperCase().indexOf("502E") != -1
				|| super.modelNumber.toUpperCase().equals("XC-RF811")) {
			isRssiEnable = true;
		}
		// 查询UTC功能
		if (readerType.equals("800")
				&& super.getModelNumber() != "XC-RF812"
				&& super.getModelNumber() != "XC-RF853") {
			SysQuery_800 msg = new SysQuery_800((byte) 0x18);
			if (super.send(msg, 2000)) {
				if (msg.getReceivedMessage().getQueryData() != null
						&& msg.getReceivedMessage().getQueryData().length > 0) {
					if (msg.getReceivedMessage().getQueryData()[0] == 0x01) {
						isUtcEnable = true;
					} else {
						isUtcEnable = false;
					}
				}
			} else {
				isUtcEnable = false;
			}
		}
	}

	/*
	 * Start.BLE连接处理
	 */
	private BluetoothLEBroadcastReceiver mBluetoothChangeReceiver = null;
	private IntentFilter mAclConnectFilter = null;
	private IntentFilter mAclDisconnectFilter = null;
	private void registerBluetoothBroadcastReceiver() {
		InvengoLog.i(TAG, "INFO.Register broadcast.");
		mAclConnectFilter = new IntentFilter(BluetoothLET.ACTION_GATT_CONNECTED);
		mAclDisconnectFilter = new IntentFilter(BluetoothLET.ACTION_GATT_DISCONNECTED);
		mBluetoothChangeReceiver = new BluetoothLEBroadcastReceiver();
		super.context.registerReceiver(mBluetoothChangeReceiver, mAclConnectFilter);
		super.context.registerReceiver(mBluetoothChangeReceiver, mAclDisconnectFilter);
	}

	private class BluetoothLEBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BluetoothLET.ACTION_GATT_CONNECTED)){
				try {
					Thread.sleep(1 * 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(doAfterActuallyConnect()){
					InvengoLog.i(TAG, "doAfterActuallyConnect.True");
					doRssiUtcQuery();
					sendBroadcast(ACTION_READER_CONNECTED);
				}else {
					sendBroadcast(ACTION_READER_DISCONNECTED);
					disConnect();
				}
			}else if(action.equals(BluetoothLET.ACTION_GATT_DISCONNECTED)){
				sendBroadcast(ACTION_READER_DISCONNECTED);
				disConnect();
			}
		}
	}

	private void sendBroadcast(String action) {
		Intent broadcastIntent = new Intent(action);
		super.context.sendBroadcast(broadcastIntent);
	}
	
	/*
	 * End.BLE连接处理
	 */


	@Override
	public void disConnect() {
		if(isConnected()){
			send(new PowerOff_800());
			super.disConnect();
			// Device Power Off
			powerControl(false);
			if(null != super.context){
				if(null != mBluetoothChangeReceiver){
					super.context.unregisterReceiver(mBluetoothChangeReceiver);
					mBluetoothChangeReceiver = null;
				}
			}
		}
	}

	public boolean wakeUp() {
		//		if(iComm instanceof RS232){//串口通信
		InvengoLog.i(TAG, "INFO.Before wakeup count {%d}", mWakeupCount);
		if(mWakeupCount <= 0){
			if (!connect()) {
				InvengoLog.e(TAG, "ERROR. wakeUp() - Failed to connect rfid reader");
				return false;
			}
		}

		mWakeupCount++;
		InvengoLog.i(TAG, "INFO.After wakeup count {%d}", mWakeupCount);
		InvengoLog.i(TAG, "INFO. wakeUp()");
		return true;
		//		}
		//		return false;
	}

	public boolean sleep() {
		//		if(iComm instanceof RS232){//串口通信
		InvengoLog.i(TAG, "INFO.Before sleep count {%d}", mWakeupCount);
		mWakeupCount--;
		if(mWakeupCount <= 0){
			//				send(new PowerOff_800());
			//				try {
			//					Thread.sleep(20);
			//				} catch (InterruptedException e) {
			//					disConnect();
			//					return true;
			//				}
			disConnect();
		}
		InvengoLog.i(TAG, "INFO.After sleep count {%d}", mWakeupCount);
		InvengoLog.i(TAG, "INFO. sleep()");
		return true;
		//		}
		//		return false;
	}

	@Override
	public boolean check() {
		if (!connect()) {
			InvengoLog.e(TAG, "ERROR. check() - Failed to connect rfid reader");
			return false;
		}

		// Wait Change Connection State
		while (!isConnected()) {
			SysUtil.sleep(10);
		}

		InvengoLog.i(TAG, "INFO. check() - [%s]", isConnected());

		boolean res = isConnected();
		disConnect();
		return res;
	}

	public void powerControl(boolean enabled) {
		DeviceType type = DeviceManager.getDeviceType();

		switch (type) {
			//		case AT911:
			//		case AT911N:
			case XC2910:
			case XC9910:
				//		case AT911_HILTI_US:
				//		case AT911_HILTI_EU:
				//		case AT511:
				//		case AT911N_HILTI_US:
				//		case AT911N_HILTI_EU:
				ModuleControl.powerRfidDevice(enabled);
				break;
			//		case XCRF1003:
			//		case AT311:
			//		case AT312:
			//			ModuleControl.powerRfidModule(false);
			//			if (enabled) {
			//				SysUtil.sleep(10);
			//				ModuleControl.powerRfidModule(true);
			//			}
			//			break;
			case XC2900:
			case XC2903:
			case XC2910_V3:
				powerXC2900Device(enabled);
				break;
			case Unknown://XC2600不做任何上电操作
				break;
			default:
				InvengoLog.e(TAG, "ERROR. powerControl(%s) - Not supported RFID module", enabled);
				return;
		}
		InvengoLog.i(TAG, "INFO. powerControl(%s)", enabled);
	}

	private boolean powerXC2900Device(boolean enabled){
		String cammand = (enabled == true) ? "91" : "90";

		boolean result = false;
		File file = new File("/proc/c620_ledctrl");
		FileWriter fr = null;
		try{
			fr = new FileWriter(file);
			fr.write(cammand);
			result = true;
		}catch(Exception e){
			result = false;
		}finally{
			try {
				if(null != fr){
					fr.close();
				}
			} catch (IOException e) {
				result = false;
			}
		}
		InvengoLog.i(TAG, "INFO.Power xc2900 & xc2903 & xc2910(V3.0) {%s}", result);
		return result;
	}

	private Object lockEvent = new Object();
	private Object lockinfo = new Object();

	public boolean send(IMessage msg) {
		if (msg instanceof ReadTag) {
			ReadTag rt = ((ReadTag) msg);
			if (msg.getIsReturn()) {
				try {
					msg.triggerOnExecuting(this);
				} catch (RuntimeException e) {
					Util.logAndTriggerApiErr(readerName, "FF26",
							e.getMessage(), LogType.Debug);
					return false;
				}
				boolean isGetOneTag = rt.isGetOneTag();
				synchronized (lockEvent) {
					synchronized (lockinfo) {
						info = new MessageInfo();
						info.setMsg(msg);
						info.setGetOneTag(isGetOneTag);
					}
					msg.setPortType(this.portType);
					byte[] sd = msg.getTransmitterData();
					boolean rc = super.send(sd);
					if (rc) {
						synchronized (info.getEv()) {
							boolean isSuc = wait(msg.getTimeOut(), info.getEv());
							if (!isSuc) {
								if (isGetOneTag) {
									msg.setStatusCode(0xff);// 超时
								} else {
									msg.setStatusCode(0x00);
								}
							}
						}
					}
					// synchronized (lockinfo) {
					// info = null;
					// }
				}
				msg.triggerOnExecuted(this);
				rt.setEndOfReading(new Object());
				wait(500, rt.getEndOfReading());
				synchronized (lockinfo) {
					info = null;
				}
				return (msg.getStatusCode() == 0x00);
			}
		}
		return super.send(msg);
	}

	public boolean wait(long timeout, Object object) {
		boolean isSuc = false;
		synchronized (object) {
			try {
				long start = System.currentTimeMillis();
				object.wait(timeout);
				long end = System.currentTimeMillis();
				long time = end - start;
				if (time >= timeout) {
					isSuc = false;
				} else {
					isSuc = true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				isSuc = false;
			}
		}
		return isSuc;
	}

	private MessageInfo info;

	class MessageInfo {
		public MessageInfo ev = this;
		public IMessage msg = null;
		public volatile boolean isGetOneTag = false;
		public volatile boolean isDone = false;

		public MessageInfo getEv() {
			return ev;
		}

		public void setEv(MessageInfo ev) {
			this.ev = ev;
		}

		public IMessage getMsg() {
			return msg;
		}

		public void setMsg(IMessage msg) {
			this.msg = msg;
		}

		public boolean isGetOneTag() {
			return isGetOneTag;
		}

		public void setGetOneTag(boolean isGetOneTag) {
			this.isGetOneTag = isGetOneTag;
		}

		public boolean isDone() {
			return isDone;
		}

		public void setDone(boolean isDone) {
			this.isDone = isDone;
		}

	}

}