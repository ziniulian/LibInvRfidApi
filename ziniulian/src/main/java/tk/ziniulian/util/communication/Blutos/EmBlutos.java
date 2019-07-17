package tk.ziniulian.util.communication.Blutos;

/**
 * 蓝牙相关的各种状态及常量
 * Created by 李泽荣 on 2019/7/5.
 */

public enum EmBlutos {
	BINDED(""),		// 已绑定事件
	BLD_NONE("没有蓝牙"),	// 没有蓝牙设备
	BLD_HEM("蓝牙未开启"),	// 蓝牙设备未开启
	BLD_INT(""),	// 蓝牙开启过程中的 Intent 返回标识
	BLD_OK(""),		// 蓝牙设备已开启
	SCANING(""),	// 正在扫描蓝牙设备
	COT_ING(""),	// 正在连接
	COT_SRVING(""),	// 正在发现服务
	COT_OK(""),		// 连接成功
	COT_ERR("连接失败"),		// 连接失败
	COT_ERRNTF("监听失败"),	// 未能成功设置连接的监听回调
	WRT_ERR("写入失败"),		// 写入失败
	;

	private String msg;
	EmBlutos(String s) {
		msg = s;
	}

	// 修改值，为以后的国际化做准备
	public void set (String s) {
		msg = s;
	}

	@Override
	public String toString() {
		return msg;
	}
}
