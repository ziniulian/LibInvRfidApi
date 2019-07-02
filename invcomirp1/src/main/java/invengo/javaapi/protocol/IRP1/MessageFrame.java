package invengo.javaapi.protocol.IRP1;

public abstract class MessageFrame {

	protected int msgType;//命令字
	protected byte[] msgLen = new byte[2];//指令长度
	protected byte[] msgBody = null;//指令内容/参数
	protected byte[] crc = new byte[2];//crc校验码

}
