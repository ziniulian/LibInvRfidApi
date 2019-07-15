package invengo.javaapi.communication;

import android.app.Activity;
import android.util.Log;

import invengo.javaapi.core.ICommunication;
import invengo.javaapi.protocol.IRP1.Decode;
import invengo.javaapi.protocol.IRP1.Reader;
import tk.ziniulian.util.communication.Blutos.BlutosDev;
import tk.ziniulian.util.communication.Blutos.BlutosLE;
import tk.ziniulian.util.communication.Blutos.EmBlutos;
import tk.ziniulian.util.communication.Blutos.InfBlutosEvt;

import static tk.ziniulian.util.Str.Bytes2Hexstr;

/**
 * 蓝牙BLE连接
 * Created by 李泽荣 on 2019/7/4.
 */

public class Ble extends ICommunication {
	private Activity ac;
	private Reader rd;
	private BlutosLE ble = new BlutosLE();
	private OnBleCheckEvt cEvt;
	private OnBleOpenEvt oEvt;
	private OnBleScanDevEvt sEvt;
	private int powStu = 0;	// 上电状态 ： 0:未连接。 1:上电。 2:断电。

	public Ble (Activity a) {
		this.ac = a;
		ble.setEvt(new InfBlutosEvt() {
			@Override
			public void onBldOk(BlutosLE self) {
				Log.i("----- 1. onBldOk -----", "onBldOk");
				if (cEvt != null) {
					cEvt.onCheck(true);
				}
			}

			@Override
			public void onErr(BlutosLE self, EmBlutos msg) {
				Log.i("----- 2. onErr -----", msg.toString());
				switch (msg) {
					case BLD_HEM:
					case BLD_NONE:
						if (cEvt != null) {
							cEvt.onCheck(false);
						}
						break;
					case COT_ERR:
					case COT_ERRNTF:
						if (oEvt != null) {
							oEvt.onOpen(false);
						}
						break;
				}
			}

			@Override
			public void onScanBegin(BlutosLE self) {
				Log.i("--- 3. onScanBegin ---", "onScanBegin");
			}

			@Override
			public void onScanOne(BlutosLE self, BlutosDev dev) {
				Log.i("----- 4.onScanOne -----", dev.getD().getAddress() + " , " + dev.getD().getBondState());
				if (sEvt != null) {
					sEvt.onScanDev(dev);
				}
			}

			@Override
			public void onScanEnd(BlutosLE self) {
				Log.i("--- 5. onScanEnd ---", self.jsonScanDevices());
				if (sEvt != null) {
					sEvt.onScanEnd();
				}
			}

			@Override
			public void onConnectBegin(BlutosLE self) {
				Log.i("-- 6. onConnectBegin --", "onConnectBegin");
			}

			@Override
			public void onConnected(BlutosLE self) {
				Log.i("---- 9.onConnected ----", "onConnected");
				// TODO: 2019/7/10 实际测试情况看，似乎不需要额外发送上电和断电指令。可待下一版本时，去除这些多余操作。
				powStu = 1;
				new Thread(){
					@Override
					public void run() {
						// 发送上电信号
						try{
							Thread.sleep(250);	// 此处不加延时，信息将会发送失败。
						} catch (Exception e) {}
						ble.wrt("5500026000402E");	// 刘名磊的开功放
						// ble.wrt("5500026001C02B");	// 赖学良的开功放
					}
				}.start();
			}

			@Override
			public void onDisConnected(BlutosLE self) {
				Log.i("-- 10.onDisConnected --", "onDisConnected");
				setConnected(false);
			}

			@Override
			public void onReceive(BlutosLE self, byte[] dat) {
				Log.i("---- 12. onReceive ----", Bytes2Hexstr(dat));
				switch (powStu) {
					case 1:	// 上电成功
						powStu = 0;
						callBaseReaderConnect();
						if (oEvt != null) {
							oEvt.onOpen(true);
						}
						break;
					case 2:	// 断电成功
						powStu = 0;
						ble.closeDevice();
						break;
					default:	// 普通的数据接收
						setBufferQueue(dat);
						break;
				}
			}
		});
	}

	// 执行 BaseReader 的 connect 方法
	private void callBaseReaderConnect () {
		iProcess = new Decode();
		iProcess.setPortType(rd.portType);
		setReader(rd);
		setReaderName(rd.readerName);

		// doAfterActuallyConnect();
		OnMsgReceived.add(rd);
		onBuffReceived.add(rd);
		threadProcess = new Thread() {
			public void run() {
				process();
			}
		};
		threadProcess.start();

		setConnected(true);
	}

	// 检查设备是否已开启蓝牙
	public void check () {
		ble.bld(ac);
	}

	// 开启蓝牙的回调
	public void bldActCb(int requestCode, int resultCode) {
		ble.bldActCb(requestCode, resultCode);
	}

	// 扫描设备
	public void scanDevice() {
		ble.clearScanDevices();
		ble.scanDevice(2000);
	}

	@Override
	public boolean open(String connString) throws Exception {
		ble.stopScanDevice();
		ble.connectDevice(ac, ble.getDev(connString), 8000);
		return false;
	}

	@Override
	public int send(byte[] data) {
		if (ble.getStu() == EmBlutos.COT_OK) {
			Log.i("-- 发出的指令 ： --", Bytes2Hexstr(data));
			ble.wrt(data);
			return data.length;
		} else {
			return 0;
		}
	}

	@Override
	public void close() {
		powStu = 2;
		ble.wrt("550001610746");	// 发送断电信号
	}

	public Ble setRd(Reader rd) {
		this.rd = rd;
		return this;
	}

	public Ble setOpenEvt(OnBleOpenEvt oEvt) {
		this.oEvt = oEvt;
		return this;
	}

	public Ble setCheckEvt(OnBleCheckEvt cEvt) {
		this.cEvt = cEvt;
		return this;
	}

	public Ble setScanDevEvt(OnBleScanDevEvt sEvt) {
		this.sEvt = sEvt;
		return this;
	}

	// 开启蓝牙监听接口
	public interface OnBleCheckEvt {
		public void onCheck(boolean ok);
	}

	// 蓝牙连接监听接口
	public interface OnBleOpenEvt {
		public void onOpen(boolean ok);
	}

	// 设备扫描接口
	public interface OnBleScanDevEvt {
		public void onScanDev(BlutosDev dev);
		public void onScanEnd();
	}
}
