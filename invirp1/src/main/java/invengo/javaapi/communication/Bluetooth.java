package invengo.javaapi.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.invengo.lib.diagnostics.InvengoLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import invengo.javaapi.core.ICommunication;
import invengo.javaapi.core.Util;

public class Bluetooth extends ICommunication {

	private BluetoothAdapter mBluetoothAdapter = null;

	private BluetoothSocket btSocket = null;

	private OutputStream writer = null;

	private InputStream reader = null;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 这条是蓝牙串口通用的UUID，不要更改

	private Thread threadRun = null;// 监听线程

	private final Object lockObj = new Object();

	@Override
	public boolean open(String connString) throws Exception {
		/* 获得通信线路过程 */

		/* 1:获取本地BlueToothAdapter */

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
		/* 3:获得Socket */
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			throw new RuntimeException("ON RESUME: Socket creation failed.");
		}
		/* 4:取消discovered节省资源 */
		mBluetoothAdapter.cancelDiscovery();
		try {
			btSocket.connect();
			super.setConnected(true);
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				super.setConnected(false);
			}
			super.setConnected(false);
		}
		if (threadRun == null || !threadRun.isAlive()) {
			threadRun = new Thread() {
				public void run() {
					runClient();
				}
			};
			threadRun.start();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		return super.isConnected();
	}

	@Override
	public void close() {
		super.setConnected(false);
		this.isConnected = false;// 再次设置
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				btSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (threadRun != null) {
			if (threadRun.isAlive()) {

			}
			threadRun = null;
		}
	}

	@Override
	public int send(byte[] data) {
		if (!super.isConnected
				|| (btSocket == null || (btSocket != null && !btSocket.isConnected()))) {
			super.isConnected = false;
			return 0;
		}
		int sl = 0;
		if (data != null) {
			try {
				if (super.isConnected()) {
					bluetoothSend(data);// 发送
					sl = data.length;
				}
			} catch (Exception e) {
				sl = 0;
			}
		} else {
		}
		return sl;
	}

	private void runClient() {
		try {
			writer = btSocket.getOutputStream();
			reader = btSocket.getInputStream();
			int readLength = 1024;
			byte[] bytes = new byte[readLength];
			int bytesRead = 0;
			byte[] receBytes = null;
			super.setConnected(btSocket.isConnected());
			while (super.isConnected()) {
				bytesRead = reader.read(bytes, 0, readLength);
				if (bytesRead > 0) {
					receBytes = new byte[bytesRead];
					System.arraycopy(bytes, 0, receBytes, 0, bytesRead);
					//					InvengoLog.i("Response", "INFO.Message Socket-Received - " + Util.convertByteArrayToHexString(receBytes));
					super.setBufferQueue(receBytes);
				} else {
					super.setConnected(false);
					break;
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (btSocket != null) {
					btSocket.close();
				}
				super.setConnected(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void bluetoothSend(byte[] sendMsg) throws Exception {
		synchronized (lockObj) {
			writer.write(sendMsg, 0, sendMsg.length);// 发送
			InvengoLog.i("Bluetooth Send", "INFO.Message Send - " + Util.convertByteArrayToHexString(sendMsg));

		}
	}

}
