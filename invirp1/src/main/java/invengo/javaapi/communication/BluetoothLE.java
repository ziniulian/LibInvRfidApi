package invengo.javaapi.communication;

import invengo.javaapi.core.ICommunication;
import invengo.javaapi.core.Util;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import com.invengo.lib.diagnostics.InvengoLog;

/**
 * BLE
 */
@Deprecated
public class BluetoothLE extends ICommunication {

	private static final String TAG = BluetoothLE.class.getSimpleName();

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothGatt mBluetoothGatt = null;
	private Object lockObj = new Object();
	//	private EncodeUtil encryptUtil = new EncodeUtil();
	private static final String BLE_SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb";
	private static final String BLE_WRITE_CHARACTERISTIC_UUID = "00001001-0000-1000-8000-00805f9b34fb";
	private static final String BLE_NOTIFY_CHARACTERISTIC_UUID = "00001002-0000-1000-8000-00805f9b34fb";
	private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	public static final String ACTION_GATT_CONNECTED = "invengo.javaapi.communication.BluetoothLE.ACTION_GATT_CONNECTED";
	public static final String ACTION_GATT_DISCONNECTED = "invengo.javaapi.communication.BluetoothLE.ACTION_GATT_DISCONNECTED";

	@Override
	public boolean open(String connString) throws Exception {
		
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
		/* 2:获取远程BlueToothDevice */
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(connString);
		if (device == null) {
			throw new RuntimeException("Can't get remote device.");
		}
		BluetoothGatt bluetoothGatt = device.connectGatt(super.context, false, mGattCallback);

		InvengoLog.i(TAG, "INFO. BluetoothLE Device Connecting.");
		return true;
		//		if(null == bluetoothGatt){
		//			return false;
		//		}else{
		//			return bluetoothGatt.connect();
		//		}
	}

	@Override
	public int send(byte[] data) {
		InvengoLog.i(TAG, "INFO. Start BluetoothLE Data Send .");
		if(!super.isConnected() || null == mBluetoothGatt){
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
		InvengoLog.i(TAG, "INFO. End BluetoothLE_Send Data Length {%s}", String.valueOf(sl));
		return sl;
	}

	/**
	 * BluetoothLE send data with BluetoothGattCharacteristic
	 */
	private boolean bluetoothLESend(byte[] data) {
		synchronized (lockObj) {
			BluetoothGattService writeGattService = mBluetoothGatt.getService(UUID.fromString(BLE_SERVICE_UUID));
			if(null != writeGattService){
				//				BluetoothGattCharacteristic writeGattCharacteristic = writeGattService.getCharacteristic(UUID.fromString(BLE_WRITE_CHARACTERISTIC_UUID));
				BluetoothGattCharacteristic writeGattCharacteristic = null;
				for(BluetoothGattCharacteristic characteristic : writeGattService.getCharacteristics()){
					if(characteristic.getUuid().toString().equals(BLE_WRITE_CHARACTERISTIC_UUID)){
						writeGattCharacteristic = characteristic;
						break;
					}
				}
				if(null != writeGattCharacteristic){
					mBluetoothGatt.setCharacteristicNotification(writeGattCharacteristic, true);
					int property = 0;
					if (((property = writeGattCharacteristic.getProperties()) & 0x4) == 4){
						InvengoLog.i(TAG, "INFO. Properties {%s}", 4);
						writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
					}else if ((property & 0x40) == 64){
						InvengoLog.i(TAG, "INFO. Properties {%s}", 64);
						writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);
					}else {
						InvengoLog.i(TAG, "INFO. Properties {%s}", 2);
						writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
					}
					//					writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
					//					writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
					//					writeGattCharacteristic.setValue(encryptUtil.encodeMessage(data));
					//					InvengoLog.i(TAG, "INFO. Actually send {%s}", Util.convertByteArrayToHexString(encryptUtil.encodeMessage(data)));
					mBluetoothGatt.setCharacteristicNotification(writeGattCharacteristic, true);
					mBluetoothGatt.writeCharacteristic(writeGattCharacteristic);
					return true;
				}else {
					InvengoLog.w(TAG, "WARN. bluetoothLESend().BluetoothGattCharacteristic is null.");
				}
			}else {
				InvengoLog.w(TAG, "WARN. bluetoothLESend().BluetoothGattService is null.");
			}
			return false;
		}
	}

	@Override
	public void close() {
		super.setConnected(false);
		super.isConnected = false;
		if(null != mBluetoothGatt){
			mBluetoothGatt.disconnect();
		}
	}

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if(newState == BluetoothGatt.STATE_CONNECTED){
				InvengoLog.i(TAG, "INFO. BluetoothLE Device Connected.");
				gatt.discoverServices();
			}else if(newState == BluetoothGatt.STATE_DISCONNECTED){
				InvengoLog.i(TAG, "INFO. BluetoothLE Device Disconnected.");
				openNotifyChannel(false);
				BluetoothLE.this.setConnected(false);
				if(null != mBluetoothGatt){
					mBluetoothGatt.close();
					mBluetoothGatt = null;
				}
				sendBroadcast(ACTION_GATT_DISCONNECTED);
			}

		};

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS){

				mBluetoothGatt = gatt;
				BluetoothLE.this.setConnected(true);

				//				List<BluetoothGattService> services = gatt.getServices();
				//				for(BluetoothGattService service : services){
				//					InvengoLog.i(TAG,"DEBUG.Service UUID {%s}", service.getUuid().toString());
				//					List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
				//					for(BluetoothGattCharacteristic characteristic : characteristics){
				//						InvengoLog.i(TAG,"DEBUG.Characteristic UUID {%s} & Property {%d}", characteristic.getUuid(), characteristic.getProperties());
				//					}
				//				}
				openNotifyChannel(true);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendBroadcast(ACTION_GATT_CONNECTED);
			}
		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
											BluetoothGattCharacteristic characteristic) {
			String uuid = characteristic.getUuid().toString();
			if(BLE_NOTIFY_CHARACTERISTIC_UUID.equals(uuid)){//Notify Characteristic uuid
				//				byte[] response = encryptUtil.decodeMessage(characteristic.getValue());
				byte[] response = null;
				BluetoothLE.this.setBufferQueue(response);
			}
		};

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
										 BluetoothGattCharacteristic characteristic, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS){
				InvengoLog.i(TAG, "INFO. Receiving BluetoothLE Read Data.");
				//				byte[] response = encryptUtil.decodeMessage(characteristic.getValue());
				byte[] response = null;
				InvengoLog.i(TAG, "INFO. Receiving Read Data {%s}.", Util.convertByteArrayToHexString(response));
				BluetoothLE.this.setBufferQueue(response);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
										  BluetoothGattCharacteristic characteristic, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS){
				InvengoLog.i(TAG, "INFO.Receiving BluetoothLE Write Data.");
				//				byte[] response = encryptUtil.decodeMessage(characteristic.getValue());
				byte[] response = null;
				InvengoLog.i(TAG, "INFO. Receiving Write Data {%s}.", Util.convertByteArrayToHexString(response));
				BluetoothLE.this.setBufferQueue(response);
			}
		};

	};

	/**
	 * BLE notify数据开关
	 */
	protected boolean openNotifyChannel(boolean enable) {
		if(null != mBluetoothGatt){
			InvengoLog.i(TAG, "INFO.BluetoothLE Notify Channel Status {%s} will set.", enable ? "OPEN" : "CLOSE");

			BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(BLE_SERVICE_UUID));
			if(null != gattService){
				BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID.fromString(BLE_NOTIFY_CHARACTERISTIC_UUID));
				if(null != gattCharacteristic){
					BluetoothGattDescriptor gattDescriptor = gattCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
					if(null != gattDescriptor){
						byte[] value = enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
						gattDescriptor.setValue(value);
						mBluetoothGatt.writeDescriptor(gattDescriptor);
						InvengoLog.i(TAG, "INFO.BluetoothLE Notify Channel Status is setting.");
						//						return mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, enable);
						return true;
					}else {
						InvengoLog.w(TAG, "INFO.openNotifyChannel().BluetoothGattDescriptor is null.");
					}
				}else {
					InvengoLog.w(TAG, "INFO.openNotifyChannel().BluetoothGattCharacteristic is null.");
				}
			}else {
				InvengoLog.w(TAG, "INFO.openNotifyChannel().BluetoothGattService is null.");
			}
		}
		return false;
	}

	protected void sendBroadcast(String action) {
		Intent broadcastIntent = new Intent(action);
		super.context.sendBroadcast(broadcastIntent);
	}

}
