package invengo.javaapi.communication;

import android.app.Activity;

import invengo.javaapi.core.ICommunication;
import invengo.javaapi.protocol.IRP1.Decode;
import invengo.javaapi.protocol.IRP1.Reader;
import tk.ziniulian.util.communication.Blutos.BlutosDev;
import tk.ziniulian.util.communication.Blutos.BlutosLE;
import tk.ziniulian.util.communication.Blutos.EmBlutos;
import tk.ziniulian.util.communication.Blutos.InfBlutosEvt;

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

	public Ble (Activity a) {
		this.ac = a;
		ble.setEvt(new InfBlutosEvt() {
			@Override
			public void onBldOk(BlutosLE self) {
				if (cEvt != null) {
					cEvt.onCheck(true);
				}
			}

			@Override
			public void onErr(BlutosLE self, EmBlutos msg) {
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
			public void onScanBegin(BlutosLE self) {}

			@Override
			public void onScanOne(BlutosLE self, BlutosDev dev) {
				if (sEvt != null) {
					sEvt.onScanDev(dev);
				}
			}

			@Override
			public void onScanEnd(BlutosLE self) {
				if (sEvt != null) {
					sEvt.onScanEnd();
				}
			}

			@Override
			public void onConnectBegin(BlutosLE self) {}

			@Override
			public void onConnected(BlutosLE self) {
				callBaseReaderConnect();
				if (oEvt != null) {
					oEvt.onOpen(true);
				}
			}

			@Override
			public void onDisConnected(BlutosLE self) {
				rd.disConnect();
				setConnected(false);
			}

			@Override
			public void onReceive(BlutosLE self, byte[] dat) {
				setBufferQueue(dat);
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
		ble.connectDevice(ac, ble.getDev(connString), 2000);
		return false;
	}

	@Override
	public int send(byte[] data) {
		if (ble.getStu() == EmBlutos.COT_OK) {
//Log.i("-- 发出的指令 ： --", Bytes2Hexstr(data));
			ble.wrt(data);
			return data.length;
		} else {
			return 0;
		}
	}

	@Override
	public void close() {
		ble.closeDevice();
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
