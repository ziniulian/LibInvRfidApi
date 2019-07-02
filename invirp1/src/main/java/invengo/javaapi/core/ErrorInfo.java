package invengo.javaapi.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class ErrorInfo {
	private Map<String, String> errMap = new HashMap<String, String>();

	public ErrorInfo() {
		if (Locale.getDefault().equals(Locale.CHINA)) {
			/* 读写器系统错误代码表 */
			errMap.put("FF", "指令在指定时间内未收到回应。可能原因读写器通信故障或者超时时间设置过短。");
			/* 读写器操作错误代码表 */
			errMap.put("12", "天线端口号错误");
			errMap.put("15", "当前读写器正执行标签操作");
			errMap.put("18", "保留");
			errMap.put("1A", "前后向功率差值过低（驻波比过大）");
			errMap.put("1F", "属于该范围但读写器无法判断或未知类型错误");
			/* 指令接收/数据传输错误代码表 */
			errMap.put("20", "接收指令或数据不完整");
			errMap.put("21", "接收指令或数据的CRC校验错误");
			errMap.put("22", "指令类型当前型号读写器不支持");
			errMap.put("23", "当前型号读写器不支持该标签协议");
			errMap.put("24", "指令参数有错误");
			errMap.put("25", "指令帧结构错误（缺少域）");
			errMap.put("26", "不支持的指令类型");
			errMap.put("27", "读写器接收到太多指令，暂时无法处理");
			errMap.put("2F", "属于该范围但读写器无法判断或未知类型错误");
			/* EPC标签操作错误代码表 */
			errMap.put("60", "标签无响应或不存在");
			errMap.put("62", "标签返回信息：标签操作地址溢出");
			errMap.put("63", "标签返回信息：操作存储区被锁定");
			errMap.put("64", "标签存取密码错误");
			errMap.put("65", "标签灭活密码错误");
			errMap.put("69", "标签返回信息：未知类型错误");
			errMap.put("6A", "标签返回信息：功率不足");
			errMap.put("6F", "属于该范围但读写器无法判断或未知类型错误");
			/* XCRF-500系列错误代码表 */
			errMap.put("3F", "发送数据校验错误");
			errMap.put("3A", "发送数据校验错误");
			/* API运行错误代码表 */
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
		} else {
			/* 读写器系统错误代码表 */
			errMap.put("FF", "Directive does not receive a response within the time specified in the. Possible reasons for reader communication failure or timeout is too short.");
			/* 读写器操作错误代码表 */
			errMap.put("12", "Erroneous antenna Number.");
			errMap.put("15", "Reader is on tag operation.");
			errMap.put("18", "Reserve");
			errMap.put("1A", "SWR too big.");
			errMap.put("1F", "The error belongs to this spectrum, but is unrecognizable by the READER or occured for unknown reason");
			/* 指令接收/数据传输错误代码表 */
			errMap.put("20", "received command or data is incomplete.");
			errMap.put("21", "received command or data CRC check error.");
			errMap.put("22", "Command type is not correspond to current reader type.");
			errMap.put("23", "Command type is not correspond to protocol of current tags.");
			errMap.put("24", "Command parameter table error.");
			errMap.put("25", ">Command data structure error.");
			errMap.put("26", "Command error.");
			errMap.put("27", "The reader is busy now. Please try again later.");
			errMap.put("2F", "The error belongs to this spectrum, but is unrecognizable by the READER or occured for unknown reason");
			/* EPC标签操作错误代码表 */
			errMap.put("60", "No tag response,or the tag is absent.");
			errMap.put("62", "Tag feedback: operation address overflow.");
			errMap.put("63", "Tag feedback: operation storage is locked.");
			errMap.put("64", "Tag Access password error.");
			errMap.put("65", "Tag destroy password error.");
			errMap.put("69", "Unknown error.");
			errMap.put("6A", "Tag feedback: Power Deficiency.");
			errMap.put("6F", "The error belongs to this spectrum, but is unrecognizable by the READER or occured for unknown reason");
			/* XCRF-500系列错误代码表 */
			errMap.put("3F", "Data check error.");
			errMap.put("3A", "Data check error.");
			/* API运行错误代码表 */
			errMap.put("FF04", ">This port is occupied.");
			errMap.put("FF05", "Send COM command failed.");
			errMap.put("FF06", "Errors.Please choose the right serial number or restart the reader and program.");
			errMap.put("FF07", "Send TCP command failed.");
			errMap.put("FF08", "No command.");
			errMap.put("FF09", "The network is disconnected.Please check.");
			errMap.put("FF10", "TCP closed for an exception.");
			errMap.put("FF11", "ErrCode file does not exist.");
			errMap.put("FF12", "Communication port failed to load.");
			errMap.put("FF13", "Failed to create Sysit.xml file.");
			errMap.put("FF14", "Cannot add a blank node.");
			errMap.put("FF15", "The target node already exists.");
			errMap.put("FF16", "The target node does not exist.");
			errMap.put("FF17", "Log failed: can not use the released resources.");
			errMap.put("FF18", "The reader configuration item does not exist in the configuration file.");
			errMap.put("FF19", "Connecte failure.");
			errMap.put("FF20", "The RS232 data recv failure.");
			errMap.put("FF21", "Send data failure.");
			errMap.put("FF22", "The connection has been disconnected.");
			errMap.put("FF23", "Data to send cannot be blank.");
			errMap.put("FF24", "There may be other user connecting.");
			errMap.put("FF25", "Intermittent read abnormal exit.");
			errMap.put("FF26", "OnExecuting failed,stop executing instructions.");
			errMap.put("FF30", "Receive data failure.");
		}
	}

	public String getErrorInfo(String errCode){
		return errMap.get(errCode);
	}

}
