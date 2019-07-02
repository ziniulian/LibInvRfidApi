package invengo.javaapi.communication;

import invengo.javaapi.core.ICommunication;
import invengo.javaapi.core.Util;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ble.api.EncodeUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;
import com.invengo.lib.diagnostics.InvengoLog;

/**
 * BLE
 */
@Deprecated
public class BluetoothLET extends ICommunication {

	private static final String TAG = BluetoothLET.class.getSimpleName();

	public BluetoothLET() {
		// TODO Auto-generated constructor stub
	}

	private BluetoothAdapter mBluetoothAdapter = null;
	//	private BluetoothGatt mBluetoothGatt = null;
	private Object lockObj = new Object();
	private EncodeUtil encryptUtil = new EncodeUtil();
	private static final String BLE_SERVICE_UUID = "00001000-0000-1000-8000-00805F9B34FB";
	private static final String BLE_WRITE_CHARACTERISTIC_UUID = "00001001-0000-1000-8000-00805F9B34FB";
	private static final String BLE_NOTIFY_CHARACTERISTIC_UUID = "00001002-0000-1000-8000-00805F9B34FB";
	private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805F9B34FB";

	public static final String ACTION_GATT_CONNECTED = "invengo.javaapi.communication.BluetoothLE.ACTION_GATT_CONNECTED";
	public static final String ACTION_GATT_DISCONNECTED = "invengo.javaapi.communication.BluetoothLE.ACTION_GATT_DISCONNECTED";

	private BleService mLeService;
	private String mMac;

	@Override
	public boolean open(String connString) throws Exception {
		this.mMac = connString;
		
		/* 1:获取本地BlueToothAdapter */
		BluetoothManager manager = (BluetoothManager) super.context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = manager.getAdapter();
		if (mBluetoothAdapter == null) {
			throw new RuntimeException("Bluetooth is not available.");
		}
		if (!mBluetoothAdapter.isEnabled()) {
			throw new RuntimeException(
					"Please enable your Bluetooth and re-run this program.");
		}
		/* 2:绑定服务 */
		bindService();

		//		/* 2:获取远程BlueToothDevice */
		//		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(connString);
		//		if (device == null) {
		//			throw new RuntimeException("Can't get remote device.");
		//		}
		//		device.connectGatt(super.context, false, mGattCallback);

		InvengoLog.i(TAG, "INFO.BluetoothLE Device Connecting.");
		return true;
	}

	/*
	 * Start BleService
	 */
	private ServiceConnection mServiceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//			InvengoLog.i(TAG, "");
			mLeService = ((BleService.LocalBinder)service).getService(mCallBack);
			mLeService.setDecode(true);
			mLeService.initialize();

			//连接BLE设备
			mLeService.connect(BluetoothLET.this.mMac, false);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mLeService = null;
		}

	};

	/**
	 * 绑定服务
	 */
	private void bindService(){
		super.context.bindService(new Intent(super.context, BleService.class),
				mServiceConnection, super.context.BIND_AUTO_CREATE);
	}

	/**
	 * 解除服务绑定
	 */
	private void unbindService(){
		super.context.unbindService(mServiceConnection);
	}

	private BleCallBack mCallBack = new BleCallBack() {

		@Override
		public void onConnected(String mac) {
			InvengoLog.i(TAG, "onConnected() - " + mac);
			//			mLeService.startReadRssi(mac, 1000);
		}

		@Override
		public void onConnectTimeout(String mac) {
			InvengoLog.w(TAG, "onConnectTimeout() - " + mac);
			BluetoothLET.this.setConnected(false);
			sendBroadcast(ACTION_GATT_DISCONNECTED);
		}

		@Override
		public void onConnectionError(String mac, int status, int newState) {
			InvengoLog.w(TAG, "onConnectionError() - " + mac + ", status = " + status + ", newState = " + newState);
			BluetoothLET.this.setConnected(false);
			sendBroadcast(ACTION_GATT_DISCONNECTED);
		}

		@Override
		public void onDisconnected(String mac) {
			InvengoLog.w(TAG, "onDisconnected() - " + mac);
			BluetoothLET.this.setConnected(false);
			sendBroadcast(ACTION_GATT_DISCONNECTED);
		}

		@Override
		public void onServicesDiscovered(String mac) {
			InvengoLog.i(TAG, "onServicesDiscovered() - " + mac);
			// !!!到这一步才可以与从机进行数据交互
			BluetoothLET.this.setConnected(true);
			sendBroadcast(ACTION_GATT_CONNECTED);
		}

		@Override
		public void onServicesUndiscovered(String mac, int status) {
			InvengoLog.e(TAG, "onServicesUndiscovered() - " + mac + ", status = " + status);
			BluetoothLET.this.setConnected(false);
			sendBroadcast(ACTION_GATT_DISCONNECTED);
		}

		//		@Override
		//		public void onCharacteristicWrite(String mac, BluetoothGattCharacteristic characteristic, int status) {
		//			InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Write Data.");
		//			if(status == BluetoothGatt.GATT_SUCCESS){
		//				InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Write Data.");
		////				byte[] response = encryptUtil.decodeMessage(characteristic.getValue());
		//				byte[] response = characteristic.getValue();
		//				byte[] afterEncrypt = encryptUtil.decodeMessage(response);
		//				InvengoLog.i(TAG, "INFO. Receiving Write Data {%s} & After Encrypt Data{%s}.", Util.convertByteArrayToHexString(response), Util.convertByteArrayToHexString(afterEncrypt));
		//				BluetoothLET.this.setBufferQueue(afterEncrypt);
		//			}
		//		};

		//		@Override
		//		public void onCharacteristicChanged(String mac, byte[] data) {
		////			byte[] response = encryptUtil.decodeMessage(characteristic.getValue());
		//			BluetoothLET.this.setBufferQueue(data);
		//		};

		@Override
		public void onCharacteristicChanged(String mac, android.bluetooth.BluetoothGattCharacteristic characteristic) {
			// 接收到从机数据
			InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Notify Data.");
			String uuid = characteristic.getUuid().toString();
			byte[] response = characteristic.getValue();
			String hexData = Util.convertByteArrayToHexString(response);
			InvengoLog.i(TAG, "onCharacteristicChanged()" + "-{" + hexData + "}");
			BluetoothLET.this.setBufferQueue(response);
		}
	};

	/*
	 * End BleService || null == mLeService
	 */

	@Override
	public int send(byte[] data) {
		InvengoLog.i("BluetoothLET.Send", "INFO.Start BluetoothLE Data Send .");
		if(!super.isConnected() || null == this.mMac){
			super.isConnected = false;
			return 0;
		}

		int sl = 0;
		if(null != data){
			if(super.isConnected()){
				bluetoothLESend(data);
				sl = data.length;
			}
		}
		InvengoLog.i("BluetoothLET.Send", "INFO.End BluetoothLE_Send Data Length {%s}", String.valueOf(sl));
		return sl;
	}

	/**
	 * BluetoothLE send data with BluetoothGattCharacteristic
	 */
	private void bluetoothLESend(byte[] data) {
		synchronized (lockObj) {
			if(null != mLeService & null != this.mMac){
				InvengoLog.i("BluetoothLET.Send", "INFO.End BluetoothLE_Send Data Length {%s}", Util.convertByteArrayToHexString(data));
				mLeService.send(this.mMac, Util.convertByteArrayToHexString(data), true);
			}

			//			BluetoothGattService writeGattService = mBluetoothGatt.getService(UUID.fromString(BLE_SERVICE_UUID));
			//			BluetoothGattCharacteristic writeGattCharacteristic = writeGattService.getCharacteristic(UUID.fromString(BLE_WRITE_CHARACTERISTIC_UUID));
			//			InvengoLog.i(TAG, "INFO.Properties {%s}", writeGattCharacteristic.getProperties());
			//			writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
			//			writeGattCharacteristic.setValue(data);
			//			mBluetoothGatt.readCharacteristic(writeGattCharacteristic);
		}
	}

	@Override
	public void close() {
		super.setConnected(false);
		super.isConnected = false;
		if(null != mLeService && null != this.mMac){
			mLeService.disconnect(this.mMac);
		}
		unbindService();
		//		if(null != mBluetoothGatt){
		//			mBluetoothGatt.disconnect();
		//		}
	}

	//	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
	//
	//		@Override
	//		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
	//			if(newState == BluetoothGatt.STATE_CONNECTED){
	//				InvengoLog.i(TAG, "INFO.BluetoothLE Device Connected.");
	//				mBluetoothGatt = gatt;
	//				BluetoothLET.this.setConnected(true);
	//				mBluetoothGatt.discoverServices();
	//				sendBroadcast(ACTION_GATT_CONNECTED);
	//			}else if(newState == BluetoothGatt.STATE_DISCONNECTED){
	//				InvengoLog.i(TAG, "INFO.BluetoothLE Device Disconnected.");
	//				openNotifyChannel(false);
	//				BluetoothLET.this.setConnected(false);
	//				mBluetoothGatt.close();
	//				mBluetoothGatt = null;
	//				sendBroadcast(ACTION_GATT_DISCONNECTED);
	//			}
	//
	//		};
	//
	//		@Override
	//		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
	//			if(status == BluetoothGatt.GATT_SUCCESS){
	//				List<BluetoothGattService> services = gatt.getServices();
	//				for(BluetoothGattService service : services){
	//					InvengoLog.i(TAG,"DEBUG.Service UUID {%s}", service.getUuid().toString());
	//					List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
	//					for(BluetoothGattCharacteristic characteristic : characteristics){
	//						InvengoLog.i(TAG,"DEBUG.Characteristic UUID {%s} & Property {%d}", characteristic.getUuid(), characteristic.getProperties());
	//					}
	//				}
	//				openNotifyChannel(true);
	//			}
	//		};
	//
	//		@Override
	//		public void onCharacteristicChanged(BluetoothGatt gatt,
	//				BluetoothGattCharacteristic characteristic) {
	//			InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Notify Data.");
	//			String uuid = characteristic.getUuid().toString();
	//			if(BLE_NOTIFY_CHARACTERISTIC_UUID.equals(uuid)){//Notify Characteristic uuid
	//				byte[] response = characteristic.getValue();
	//				BluetoothLET.this.setBufferQueue(response);
	//			}
	//		};
	//
	//		@Override
	//		public void onCharacteristicRead(BluetoothGatt gatt,
	//				BluetoothGattCharacteristic characteristic, int status) {
	//			super.onCharacteristicRead(gatt, characteristic, status);
	//			InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Read Data.");
	//			if(status == BluetoothGatt.GATT_SUCCESS){
	//				byte[] response = characteristic.getValue();
	//				BluetoothLET.this.setBufferQueue(response);
	//			}
	//		}
	//
	//		@Override
	//		public void onCharacteristicWrite(BluetoothGatt gatt,
	//				BluetoothGattCharacteristic characteristic, int status) {
	//			InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Write Data.");
	//			if(status == BluetoothGatt.GATT_SUCCESS){
	//				byte[] response = characteristic.getValue();
	//				BluetoothLET.this.setBufferQueue(response);
	//			}
	//		};
	//
	//	};

	/**
	 * BLE notify数据开关
	 */
	//	protected boolean openNotifyChannel(boolean enable) {
	//		if(null != mBluetoothGatt){
	//			InvengoLog.i(TAG, "INFO.BluetoothLE Notify Channel Status {%s}", enable ? "OPEN" : "CLOSE");
	//
	//			BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(BLE_SERVICE_UUID));
	//			BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(BLE_NOTIFY_CHARACTERISTIC_UUID));
	//			BluetoothGattDescriptor gattDescriptor = gattCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
	//			if(null != gattDescriptor){
	//				byte[] value = enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
	//				gattDescriptor.setValue(value);
	//				mBluetoothGatt.writeDescriptor(gattDescriptor);
	//			}
	//			return mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, enable);
	//		}
	//		return false;
	//	}

	protected void sendBroadcast(String action) {
		Intent broadcastIntent = new Intent(action);
		super.context.sendBroadcast(broadcastIntent);
	}

}
