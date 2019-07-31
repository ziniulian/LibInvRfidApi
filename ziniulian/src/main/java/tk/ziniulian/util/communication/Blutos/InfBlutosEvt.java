package tk.ziniulian.util.communication.Blutos;

/**
 * 蓝牙相关事件
 * Created by 李泽荣 on 2019/7/5.
 */

public interface InfBlutosEvt {
	public void onErr (BlutosLE self, EmBlutos msg);	// 错误处理
	public void onBldOk (BlutosLE self);	// 蓝牙开启成功

	public void onScanBegin (BlutosLE self);	// 开始扫描蓝牙设备
	public void onScanOne (BlutosLE self, BlutosDev dev);	// 扫描到一个设备
	public void onScanEnd (BlutosLE self);	// 停止扫描蓝牙设备

	public void onConnectBegin (BlutosLE self);	// 开始连接
	public void onConnected (BlutosLE self);	// 已连接
	public void onDisConnected (BlutosLE self);	// 断开连接

	public void onReceive (BlutosLE self, byte[] dat);	// 接收消息
	public void onPower (BlutosLE self, int p);	// 电量信息
}
