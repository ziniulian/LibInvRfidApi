package invengo.javaapi.core;

import java.util.HashMap;
import java.util.Map;

public abstract class ErrorInfo {

	public static final Map<String, String> errMap = new HashMap<String, String>();

	static{
		/*读写器系统错误代码表*/
		errMap.put("FF", "指令在指定时间内未收到回应。可能原因读写器通信故障或者超时时间设置过短。");
		/*读写器操作错误代码表*/
		errMap.put("12", "天线端口号错误");
		errMap.put("15", "当前读写器正执行标签操作");
		errMap.put("18", "保留");
		errMap.put("1A", "天线不匹配");
		errMap.put("1F", "属于该范围但读写器无法判断或未知类型错误");
		/*指令接收/数据传输错误代码表*/
		errMap.put("20", "接收指令或数据不完整");
		errMap.put("21", "接收指令或数据的CRC校验错误");
		errMap.put("22", "指令类型当前型号读写器不支持");
		errMap.put("23", "当前型号读写器不支持该标签协议");
		errMap.put("24", "指令参数有错误");
		errMap.put("25", "指令帧结构错误（缺少域）");
		errMap.put("26", "不支持的指令类型");
		errMap.put("27", "读写器接收到太多指令，暂时无法处理");
		errMap.put("2F", "属于该范围但读写器无法判断或未知类型错误");
		/*EPC标签操作错误代码表*/
		errMap.put("60", "标签无响应或不存在");
		errMap.put("62", "标签返回信息：标签操作地址溢出");
		errMap.put("63", "标签返回信息：操作存储区被锁定");
		errMap.put("64", "标签存取密码错误");
		errMap.put("65", "标签灭活密码错误");
		errMap.put("69", "标签返回信息：未知类型错误");
		errMap.put("6A", "标签返回信息：功率不足");
		errMap.put("6F", "属于该范围但读写器无法判断或未知类型错误");
		/*XCRF-500系列错误代码表*/
		errMap.put("3F", "发送数据校验错误");
		errMap.put("3A", "发送数据校验错误");
		/*API运行错误代码表*/
		errMap.put("FF01", "程序错误，请重新发送指令！");
		errMap.put("FF02", "发送Usb指令失败！");
		errMap.put("FF03", "Usb程序错误，请重新发送指令！");
		errMap.put("FF04", "端口已被占用！");
		errMap.put("FF05", "发送Com指令失败！");
		errMap.put("FF06", "程序错误，请选择正确端口号或重启读写器和程序！");
		errMap.put("FF07", "发送Tcp指令失败！");
		errMap.put("FF08", "发送信息为空！");
		errMap.put("FF09", "网络已经断开，请检查是否掉线！");
		errMap.put("FF10", "TCP异常关闭。");
		errMap.put("FF11", "ErrCode文件不存在。");
		errMap.put("FF12", "通讯端口加载失败");
		errMap.put("FF13", "创建Sysit.xml文件失败。");
		errMap.put("FF14", "不能添加空节点。");
		errMap.put("FF15", "目标节点已经存在。");
		errMap.put("FF16", "目标节点不存在。");
		errMap.put("FF17", "日志记录失败：不能使用被释放的资源");
		errMap.put("FF18", "配置文件中不存在该读写器配置项。");
		errMap.put("FF19", "建立连接失败。");
		errMap.put("FF20", "RS232数据接收失败。");
		errMap.put("FF21", "发送数据失败。");
		errMap.put("FF22", "连接已断开。");
		errMap.put("FF23", "发送数据不能为空。");
		errMap.put("FF24", "可能有另外用户连接，当前连接作废。");
		errMap.put("FF25", "间歇读取异常退出。");
		errMap.put("FF26", "OnExecuting执行失败，指令中止执行。");
		errMap.put("FF30", "数据接收失败。");
	}
}
