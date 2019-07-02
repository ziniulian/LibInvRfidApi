package invengo.javaapi.protocol.IRP1;

/**
 * 500系列客户端模式服务器IP设置
 *
 * @author dp732
 *
 */
public class PcIpsConfig_500 extends BaseMessage {

	/**
	 * @param serverCount
	 *            服务器个数
	 * @param ips
	 *            服务器IP序列
	 */
	public PcIpsConfig_500(byte serverCount, byte[] ips) {
		super.msgBody = new byte[1 + ips.length];
		super.msgBody[0] = serverCount;
		System.arraycopy(ips, 0, super.msgBody, 1, ips.length);
	}

	public PcIpsConfig_500() {

	}
}
