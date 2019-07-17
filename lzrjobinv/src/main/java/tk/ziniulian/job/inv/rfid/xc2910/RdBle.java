package tk.ziniulian.job.inv.rfid.xc2910;

import invengo.javaapi.communication.Ble;
import invengo.javaapi.protocol.IRP1.Reader;
import tk.ziniulian.job.inv.rfid.EmCb;

/**
 * 基于BLE通信的通用读写器
 * Created by 李泽荣 on 2019/7/10.
 */

public class RdBle extends Rd {
	private Ble b;
	private String devAdr = "";

	// 连接设备
	private Runnable connectRaBle = new Runnable() {
		@Override
		public void run() {
			rd.connect();
		}
	};

	// 构造函数
	public RdBle (Ble ble) {
		super();
		this.b = ble;

		// 蓝牙连接事件处理
		b.setOpenEvt(new Ble.OnBleOpenEvt() {
			@Override
			public void onOpen(boolean ok) {
				isConnect = ok;
				cb(EmCb.HidProgress);
				if (isConnect) {
					cb(EmCb.Connected);
				} else {
					cb(EmCb.ErrConnect);
				}
			}
		});
	}

	@Override
	protected Reader crtRd () {
		return new Reader("LZR_BLE", devAdr, b);
	}

	@Override
	public void open() {
		if (!isConnect && rd != null) {
			cb(EmCb.ShowProgress);
			rd.connect();

//			new Thread(connectRaBle).start();	// BLE 的开关尽量放在主线程中执行
		}
	}

	public String getDevAdr() {
		return devAdr;
	}

	public RdBle setDevAdr(String devAdr) {
		this.devAdr = devAdr;
		if (rd != null) {
			rd.connStr = devAdr;
		}
		return this;
	}
}
