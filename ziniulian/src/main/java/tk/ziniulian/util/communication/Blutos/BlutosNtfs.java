package tk.ziniulian.util.communication.Blutos;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 开关蓝牙 notify
 * Created by 李泽荣 on 2019/7/29.
 */

public class BlutosNtfs implements Runnable {
	private Map<String, UUID> us = new HashMap<String, UUID>();	// UUID集合
	private BluetoothGatt bg;
	private BluetoothGattService srv = null;	// GATT 的 Service
	private BluetoothGattCharacteristic ntf;	// 用于开关的 notify
	private BluetoothGattDescriptor dec;	// 用于开关 notify 的 Descriptor

	private int tim = 0;	// 线程开关前的延迟时间
	private int typ = 1;	// 线程操作类型

	public BlutosNtfs (String srv, String ntf, String dec) {
		addId("srv", srv);
		addId("ntf", ntf);
		addId("dec", dec);
	}

	public BlutosNtfs (String srv, String ntf) {
		addId("srv", srv);
		addId("ntf", ntf);
		addId("dec", "00002902-0000-1000-8000-00805f9b34fb");
	}

	@Override
	public void run() {
		try {
			if (tim > 0) {
				Thread.sleep(tim);	// 不做延时，有时notify开启后会无响应
			}
			switch (typ) {
				case 1:		// 直接打开监听（用于读取数据）
					open();
					break;
				case 2:		// 先读一次，再打开监听（用于读取电量，因为电压不变时直接打开监听不会立即获得数值，除非电量发生变化时才返回数值。）
					read();
					if (tim > 0) {
						Thread.sleep(tim);	// 不做延时，notify开启会无响应
					}
					open();
					break;
			}
		} catch (Exception e) {}
	}

	// 添加一个UUID
	public BlutosNtfs addId (String nam, String uuid) {
		try {
			UUID u = UUID.fromString(uuid);
			us.put(nam, u).toString().equals(uuid);
		} catch (Exception e) {}
		return this;
	}

	// 获取一个UUID
	public UUID getId (String nam) {
		return us.get(nam);
	}

	// 获取 notify 的 UUID
	public UUID getNtfId () {
		return us.get("ntf");
	}

	// 初始化
	public boolean init (BluetoothGatt gatt) {
		boolean r = false;
		this.bg = gatt;
		srv = bg.getService(us.get("srv"));
		if (srv != null) {
			ntf = srv.getCharacteristic(us.get("ntf"));
			if (ntf != null) {
				dec = ntf.getDescriptor(us.get("dec"));
				if (dec != null) {
					r = true;
				}
			}
		}
		return r;
	}

	// 获取一个 Characteristic
	public BluetoothGattCharacteristic getChr (String nam) {
		BluetoothGattCharacteristic r = null;
		if (srv != null) {
			r = srv.getCharacteristic(us.get(nam));
		}
		return r;
	}

	// 开关
	public boolean swich (boolean enable) {
		boolean r = false;
		if (dec != null) {
			if (bg.setCharacteristicNotification(ntf, enable)) {	// 开启 或 关闭 Notify Characteristic 的回调
				dec.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
				r = bg.writeDescriptor(dec);	// 设置 Notify 的 Descriptor 的值
			}
		}
		return false;
	}

	// 打开 notify
	public boolean open () {
		return swich (true);
	}

	// 关闭 notify
	public boolean close () {
		return swich (false);
	}

	// 读取 notify
	public boolean read () {
		return bg.readCharacteristic(ntf);
	}

	// 在新线程中打开 notify
	public void openT (int t, int p) {
		this.tim = t;
		this.typ = p;
		new Thread(this).start();
	}

	// 在新线程中打开 notify
	public void openT (int t) {
		openT(t, 1);
	}

	// 在新线程中打开 notify
	public void openT () {
		openT(0, 1);
	}

}
