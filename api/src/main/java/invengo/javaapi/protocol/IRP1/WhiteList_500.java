package invengo.javaapi.protocol.IRP1;

/**
 * 500系列白名单配置
 *
 * @author dp732
 *
 */
public class WhiteList_500 extends BaseMessage {

	/**
	 * @param setType
	 *            配置类型： 1=启用白名单功能；其他：取消白名单功能；
	 */
	public WhiteList_500(byte setType) {
		super.msgBody = new byte[] { setType };
	}

	public WhiteList_500() {
	}
}
