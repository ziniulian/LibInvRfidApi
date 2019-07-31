package invengo.javaapi.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import invengo.javaapi.core.ICommunication;
import tk.ziniulian.util.communication.Blutos.BlutosNtfs;

/**
 * BLE
 */
public class BluetoothLE extends ICommunication {

	private static final String TAG = BluetoothLE.class.getSimpleName();

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothGatt mBluetoothGatt = null;
	private Object lockObj = new Object();
	//	private EncodeUtil encryptUtil = new EncodeUtil();

	// 电量UUID
	private BlutosNtfs powntf = new BlutosNtfs(
		"0000180f-0000-1000-8000-00805f9b34fb",
		"00002a19-0000-1000-8000-00805f9b34fb"
	);
	// 读写UUID
	private BlutosNtfs wrntf = new BlutosNtfs(
		"0000fff0-0000-1000-8000-00805f9b34fb",
		"0000fff1-0000-1000-8000-00805f9b34fb"
	).addId("wrt", "0000fff2-0000-1000-8000-00805f9b34fb");

	public static final String ACTION_GATT_CONNECTED = "invengo.javaapi.communication.BluetoothLE.ACTION_GATT_CONNECTED";
	public static final String ACTION_GATT_DISCONNECTED = "invengo.javaapi.oocommunication.BluetthLE.ACTION_GATT_DISCONNECTED";
	public static final String ACTION_GATT_DEVPOW = "invengo.javaapi.oocommunication.BluetthLE.ACTION_GATT_DevicePower";

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

//		InvengoLog.i(TAG, "INFO. BluetoothLE Device Connecting.");
		return true;
	}

	@Override
	public int send(byte[] data) {
//		InvengoLog.i(TAG, "INFO. Start BluetoothLE Data Send .");
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
//		InvengoLog.i(TAG, "INFO. End BluetoothLE_Send Data Length {%s}", String.valueOf(sl));
		return sl;
	}

	/**
	 * BluetoothLE send data with BluetoothGattCharacteristic
	 */
	private boolean bluetoothLESend(byte[] data) {
		synchronized (lockObj) {
				BluetoothGattCharacteristic writeGattCharacteristic = null;
				writeGattCharacteristic = wrntf.getChr("wrt");
				if(null != writeGattCharacteristic){
					//					mBluetoothGatt.setCharacteristicNotification(writeGattCharacteristic, true);
					int property = 0;
					if (((property = writeGattCharacteristic.getProperties()) & 0x4) == 4){
//						InvengoLog.i(TAG, "INFO. Properties {%s}", 4);
						writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
					}else if ((property & 0x40) == 64){
//						InvengoLog.i(TAG, "INFO. Properties {%s}", 64);
						writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_SIGNED);
					}else {
//						InvengoLog.i(TAG, "INFO. Properties {%s}", 2);
						writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
					}
					writeGattCharacteristic.setValue(data);
//					InvengoLog.i(TAG, "INFO. Actually send {%s}", Util.convertByteArrayToHexString(data));
					mBluetoothGatt.writeCharacteristic(writeGattCharacteristic);
					return true;
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
//				InvengoLog.i(TAG, "INFO. BluetoothLE Device Connected.");
				gatt.discoverServices();
			}else if(newState == BluetoothGatt.STATE_DISCONNECTED){
//				InvengoLog.i(TAG, "INFO. BluetoothLE Device Disconnected.");
				wrntf.close();
				powntf.close();
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

				if (wrntf.init(gatt) && powntf.init(gatt)) {
					wrntf.open();
					sendBroadcast(ACTION_GATT_CONNECTED);
					powntf.openT(250, 2);
				}
			}
		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			UUID uuid = characteristic.getUuid();
			if(wrntf.getNtfId().equals(uuid)){
//				byte[] response = encryptUtil.decodeMessage(characteristic.getValue());
				byte[] response = characteristic.getValue();
//				InvengoLog.i(TAG, "INFO. Receiving Read Data {%s}.", Util.convertByteArrayToHexString(response));
				BluetoothLE.this.setBufferQueue(response);
			} else if (powntf.getNtfId().equals(uuid)) {
				sendPowBroadcast(characteristic.getValue()[0] + "");
			}
		};

		@Override
		public void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(powntf.getNtfId())){
				sendPowBroadcast(characteristic.getValue()[0] + "");
			}
		}

	};

	protected void sendBroadcast(String action) {
		Intent broadcastIntent = new Intent(action);
		super.context.sendBroadcast(broadcastIntent);
	}

	private void sendPowBroadcast(String p) {
		Intent broadcastIntent = new Intent(ACTION_GATT_DEVPOW);
		broadcastIntent.putExtra("pow", p);
		super.context.sendBroadcast(broadcastIntent);
	}
}
