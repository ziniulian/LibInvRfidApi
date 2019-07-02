package invengo.javaapi.protocol.IRP1;

/**
 * 500系列白名单下载
 *
 * @author dp732
 *
 */
public class WhiteListDownLoad_500 extends BaseMessage {

	/**
	 * @param data
	 *            数据内容：wget -c -O username ftp://username:password@IP/filename
	 */
	public WhiteListDownLoad_500(byte[] data) {
		// 指令内容
		super.msgBody = data;
	}

	public WhiteListDownLoad_500() {
	}
}
