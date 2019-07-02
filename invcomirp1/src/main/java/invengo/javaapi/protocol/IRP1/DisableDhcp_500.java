package invengo.javaapi.protocol.IRP1;

/**
 * 500系列关闭DHCP功能
 *
 * @author dp732
 *
 */
public class DisableDhcp_500 extends BaseMessage {

	/**
	 * @param netInfo
	 *            12个字节，分别为IP地址、子网掩码及网关三个部分。在关闭DHCP功能后，就是静态设置IP地址。
	 */
	public DisableDhcp_500(byte[] netInfo) {
		super.msgBody = netInfo;
	}

	public DisableDhcp_500() {

	}
}
