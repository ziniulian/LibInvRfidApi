package tk.ziniulian.util.communication.Blutos;

import android.bluetooth.BluetoothDevice;

/**
 * 扫描到的蓝牙设备信息
 * Created by 李泽荣 on 2019/7/5.
 */

public class BlutosDev {
	private BluetoothDevice d;
	private int rssi;	// 信号强度

	public BlutosDev (BluetoothDevice bd, int r) {
		this.d = bd;
		this.rssi = r;
	}

	public BluetoothDevice getD() {
		return d;
	}

	public BlutosDev setRssi(int rssi) {
		this.rssi = rssi;
		return this;
	}

	public String toJson () {
		StringBuilder s = new StringBuilder("{\"nam\":\"");
		if (d.getName() != null) s.append(d.getName());
		s.append("\",\"adr\":\"");
		s.append(d.getAddress());
		s.append("\",\"rsi\":");
		s.append(rssi);
		s.append(",\"typ\":");
		s.append(d.getType());
		s.append("}");
		return s.toString();
	}
}
