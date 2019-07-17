package tk.ziniulian.util.communication.Blutos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;
import static tk.ziniulian.util.Str.Hexstr2Bytes;
import static tk.ziniulian.util.communication.Blutos.EmBlutos.COT_ERR;
import static tk.ziniulian.util.communication.Blutos.EmBlutos.COT_ING;
import static tk.ziniulian.util.communication.Blutos.EmBlutos.COT_SRVING;
import static tk.ziniulian.util.communication.Blutos.EmBlutos.SCANING;

/**
 * 低功耗蓝牙 BLE
 * Created by 李泽荣 on 2019/7/5.
 */

public class BlutosLE {
	private BluetoothManager bm = null;
	private BluetoothAdapter ba = null;        // 蓝牙句柄
	private EmBlutos stu = null;	// 状态
	private InfBlutosEvt evt;		// 事件
	private ScanCb scb = new ScanCb(this);	// 扫描回调

	private BluetoothGatt bg = null;	// 连接句柄
	// Feasycom 设备的UUID
	private UUID srvUid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");    // Service UUID
	private UUID wrtUid = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");    // 写入 UUID
	private UUID ntfUid = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");    // 监听 UUID
	private UUID ntfDecUid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");    // 监听的 Descriptor UUID
	private GattCb gcb = new GattCb(this);	// 连接回调
	BluetoothGattCharacteristic wrtChr = null;

	// 绑定事件
	public BlutosLE setEvt(InfBlutosEvt e) {
		if (stu == null) {
			this.evt = e;
			stu = EmBlutos.BINDED;
		}
		return this;
	}

	public EmBlutos getStu() {
		return stu;
	}

	// 开启蓝牙
	public void bld(Activity a) {
		if (stu == EmBlutos.BINDED) {
			bm = (BluetoothManager) a.getSystemService(BLUETOOTH_SERVICE);
			ba = bm.getAdapter();
			if (ba == null) {
				stu = EmBlutos.BLD_NONE;    // 没有蓝牙
				evt.onErr(this, EmBlutos.BLD_NONE);
			} else {
				stu = EmBlutos.BLD_HEM;
				if (ba.isEnabled()) {
					// 蓝牙已开启
					bldPerm();
				} else {
					// 蓝牙未开启，向系统发送打开蓝牙的请求
					Intent bldit = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					a.startActivityForResult(bldit, EmBlutos.BLD_INT.ordinal());
				}
			}
		}
	}

	// 开启蓝牙的回调
	public void bldActCb (int requestCode, int resultCode) {
		if (requestCode == EmBlutos.BLD_INT.ordinal()) {
			if (resultCode == Activity.RESULT_OK) {
				bldPerm ();
			} else {
				evt.onErr(this, EmBlutos.BLD_HEM);
			}
		}
	}

	// 动态权限申请
	public void bldPerm () {
		// TODO: 2019/7/5 安卓6.0以上版本需要动态申请定位权限，才能正常使用蓝牙功能。
		stu = EmBlutos.BLD_OK;
		evt.onBldOk(this);
	}

	// 扫描设备
	public void scanDevice(int t) {
		closeDevice();
		if (stu == EmBlutos.BLD_OK) {
			stu = SCANING;
			ba.startLeScan(scb);    // 开始扫描 。 安卓7.0，限制30秒内，最多连接5次。
			if (t != 0) {
				scb.autoStop(t, 1);    // t 毫秒后自动停止扫描
			}
			evt.onScanBegin(this);
		}
	}

	// 停止扫描设备
	public void stopScanDevice() {
		if (stu == SCANING) {
			scb.stopAutoStop();
			ba.stopLeScan(scb);    // 结束扫描
			stu = EmBlutos.BLD_OK;
			evt.onScanEnd(this);
		}
	}

	// 扫描设备（默认3秒）
	public void scanDevice() {
		scanDevice(3000);
	}

	// 获取扫描到的设备列表
	public Map<String, BlutosDev> getScanDevices() {
		return scb.ds;
	}

	// 清空扫描到的设备列表
	public void clearScanDevices() {
		scb.ds.clear();
	}

	// 获取扫描到的设备列表的JSON格式
	public String jsonScanDevices() {
		if (scb.ds.isEmpty()) {
			return "[]";
		} else {
			StringBuilder s = new StringBuilder("[");
			for (Map.Entry<String, BlutosDev> e : scb.ds.entrySet()) {
				s.append(e.getValue().toJson());
				s.append(",");
			}
			s.deleteCharAt(s.length() - 1);
			s.append(']');
			return s.toString();
		}
	}

	// 获取蓝牙设备
	public BluetoothDevice getDev(String addr) {
		if (scb.ds.containsKey(addr)) {
			return scb.ds.get(addr).getD();
		} else {
			try {
				BluetoothDevice d = ba.getRemoteDevice(addr);	// 此处设备地址的格式不正确将会抛出异常。若地址正确，但设备不存在依然会正常返回一个正常的对象。
				scb.ds.put(addr, new BlutosDev(d, 1));
				return d;
			} catch (Exception e) {
				return null;
			}
		}
	}

	// 连接设备
	public void connectDevice(Context c, BluetoothDevice d, int tim) {
		if (stu == EmBlutos.BLD_OK && d != null){
			stu = COT_ING;

//			if (Build.VERSION.SDK_INT >= 23) {
//				bg = d.connectGatt(c, true, gcb, TRANSPORT_LE);	// 安卓6.0以上必须这么写
//			} else {
//				bg = d.connectGatt(c, true, gcb);	// 当 autoConnect 为 true 时，连接速度会很慢。
				bg = d.connectGatt(c, false, gcb);
//			}

			evt.onConnectBegin(this);
			if (tim > 0) {
				// 此处若不设超时，蓝牙会一直等待，直至连接上蓝牙设备为止。
				scb.autoStop(tim, 2);
			}
		}
	}

	// 连接设备(默认10秒)
	public void connectDevice(Context c, BluetoothDevice d) {
		connectDevice(c, d, 10000);
	}

	// 关闭连接
	public void closeDevice() {
		if (bg != null) {
			bg.disconnect();
			if (bg != null) {	// 不知为何，此处经常会报空指针异常，故需加以判断。
				bg.close();
				bg = null;
				EmBlutos e = stu;
				stu = EmBlutos.BLD_OK;
				switch (e) {
					case COT_OK:
						evt.onDisConnected(this);
						break;
					case COT_ING:
					case COT_ERR:
						evt.onErr(this, EmBlutos.COT_ERR);
						break;
					case COT_ERRNTF:
					case WRT_ERR:
						evt.onErr(this, e);
						break;
				}
			}
		}
		stopScanDevice();
	}

	// 设置UUID
	public BlutosLE setSrvUid(UUID srvUid) {
		this.srvUid = srvUid;
		return this;
	}

	public BlutosLE setWrtUid(UUID wrtUid) {
		this.wrtUid = wrtUid;
		return this;
	}

	public BlutosLE setNtfUid(UUID ntfUid) {
		this.ntfUid = ntfUid;
		return this;
	}

	public BlutosLE setNtfDecUid(UUID ntfDecUid) {
		this.ntfDecUid = ntfDecUid;
		return this;
	}

	// 自动获取UUID
	private HashMap<String, UUID> getUuid() {
		HashMap<String, UUID> r = new HashMap<String, UUID>();

		List<BluetoothGattService> bluetoothGattServices = bg.getServices();
		for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
			List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
			for (BluetoothGattCharacteristic characteristic : characteristics) {
				int charaProp = characteristic.getProperties();
				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
					r.put("read_UUID_chara", characteristic.getUuid());
					r.put("read_UUID_service", bluetoothGattService.getUuid());
				}
				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
					r.put("write_UUID_chara", characteristic.getUuid());
					r.put("write_UUID_service", bluetoothGattService.getUuid());
				}
				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
					// r.put("write_UUID_chara", characteristic.getUuid());
					// r.put("write_UUID_service", bluetoothGattService.getUuid());
				}
				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
					r.put("notify_UUID_chara", characteristic.getUuid());
					r.put("notify_UUID_service", bluetoothGattService.getUuid());
				}
				if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
					r.put("indicate_UUID_chara", characteristic.getUuid());
					r.put("indicate_UUID_service", bluetoothGattService.getUuid());
				}
			}
		}
		return r;
	}

	// 写数据
	public void wrt(String hex) {
		wrt(Hexstr2Bytes(hex));
	}

	// 写数据
	public void wrt(byte[] data) {
		if (stu == EmBlutos.COT_OK) {
			if (data.length > 20) {    // 数据大于20个字节 分批次写入
				int num;
				if (data.length % 20 != 0) {
					num = data.length / 20 + 1;
				} else {
					num = data.length / 20;
				}
				for (int i = 0; i < num; i++) {
					byte[] tempArr;
					if (i == num - 1) {
						tempArr = new byte[data.length - i * 20];
						System.arraycopy(data, i * 20, tempArr, 0, data.length - i * 20);
					} else {
						tempArr = new byte[20];
						System.arraycopy(data, i * 20, tempArr, 0, 20);
					}
					wrtChr.setValue(tempArr);
					bg.writeCharacteristic(wrtChr);
				}
			} else if (data.length > 0) {
				wrtChr.setValue(data);
				bg.writeCharacteristic(wrtChr);
			}
		}
	}

	/************** 内部类 **************/

	// 自动停止线程
	private class StopRa implements Runnable {
		private int tim = 0;    // 时间间隔
		private int typ = 0;	// 1:SCANING; 2:COT_ING
		private Thread t = null;

		public StopRa (int t, int p) {
			this.tim = t;
			this.typ = p;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(tim);
				if (!Thread.currentThread().isInterrupted()) {
					if (typ == 1 && stu == SCANING) {
						stopScanDevice();
					} else if (typ == 2 && stu == COT_ING) {
						closeDevice();
					}
				}
			} catch (Exception e) {}
		}

		// 启动线程
		public StopRa start() {
			stop();
			t = new Thread(this);
			t.start();
			return this;
		}

		// 终止线程
		public void stop() {
			if (t != null) {
				typ = 0;
				t.interrupt();
				t = null;
			}
		}
	}

	// 扫描设备的回调
	private class ScanCb implements BluetoothAdapter.LeScanCallback {
		private HashMap<String, BlutosDev> ds = new HashMap<String, BlutosDev>();    // 扫描到的设备列表
		private BlutosLE self;
		private StopRa sr = null;

		public ScanCb(BlutosLE b) {
			this.self = b;
		}

		@Override
		public void onLeScan(BluetoothDevice d, int rsi, byte[] bytes) {
			String addr = d.getAddress();
			if (!ds.containsKey(addr)) {
				BlutosDev bd = new BlutosDev(d, rsi);
				ds.put(addr, bd);
				evt.onScanOne(self, bd);
			} else {
				ds.get(addr).setRssi(rsi);
			}
		}

		// 自动停止
		public void autoStop (int t, int p) {
			stopAutoStop();
			sr = new StopRa(t, p).start();
		}

		// 终止自动停止的线程
		public void stopAutoStop () {
			if (sr != null) {
				sr.stop();
				sr = null;
			}
		}
	}

	// 连接回调
	private class GattCb extends BluetoothGattCallback {
		private BlutosLE self;

		public GattCb(BlutosLE b) {
			this.self = b;
		}

		// 断开或连接 状态发生变化时调用
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//Log.i("------", "001 , " + status + " , " + newState);
			if (status == BluetoothGatt.GATT_SUCCESS) {	// 连接成功
				switch (newState) {
					case BluetoothProfile.STATE_CONNECTED :	// 已连接
						stu = COT_SRVING;
						gatt.discoverServices();	// 开始发现服务
						break;
					case BluetoothProfile.STATE_DISCONNECTED:
						closeDevice();
						break;
				}
			} else {	// 连接失败
				if (status == BluetoothProfile.GATT_SERVER && newState == BluetoothProfile.STATE_DISCONNECTED) {
					stu = COT_ERR;	// 蓝牙异常中断
				}
				closeDevice();
			}

			super.onConnectionStateChange(gatt, status, newState);
		}

		// 发现设备（真正建立连接）
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// 直到这里才是真正建立了可通信的连接，但这里不能直接执行 write、read 等操作，会导致线程阻塞。
			stu = EmBlutos.COT_ERRNTF;
			new Thread() {
				@Override
				public void run() {
					if (bg != null) {
						BluetoothGattService srv = bg.getService(srvUid);
						if (srv != null) {
							BluetoothGattCharacteristic ntfBgc = srv.getCharacteristic(ntfUid);
							if (ntfBgc != null) {
								if (bg.setCharacteristicNotification(ntfBgc, true)) {	// 设置 Notify Characteristic 的回调
									BluetoothGattDescriptor bgd = ntfBgc.getDescriptor(ntfDecUid);	// 获取 Descriptor
									if (bgd != null) {
										bgd.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);	// 设置 Descriptor 可以返回消息
										if (bg.writeDescriptor(bgd)) {
											wrtChr = srv.getCharacteristic(wrtUid);	// 获取 Write Characteristic
											if (wrtChr != null) {
												stu = EmBlutos.COT_OK;
												evt.onConnected(self);
												return;
											}
										}
									}
								}
							}
						}
						closeDevice();
					}
				}
			}.start();

			super.onServicesDiscovered(gatt, status);
		}

		/*
		// 读操作的回调
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			Log.i("--------", "Read : status=" + status + ",value=" + Bytes2Hexstr(characteristic.getValue()));
		}
		*/

		// 写操作的回调
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			if (status != 0) {
				stu = EmBlutos.WRT_ERR;	// 要么连接断开，要么写入成功，基本不会存在写入失败的情况。但还是很好奇的加入了一个写入失败的事件。
				closeDevice();
			}
		}

		// 接收到硬件返回的数据
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			byte[] dat = characteristic.getValue();
			if (dat != null && dat.length > 0) {
				evt.onReceive(self, dat);
			}
		}
	}
}
