package invengo.javaapi.protocol.IRP1;

/**
 * 500系列频率设置
 *
 * @author zxq943
 *
 */
public class FreqConfig_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            信息类型：
	 * @param infoParam
	 *            信息参数
	 */
	public FreqConfig_500(byte infoType, byte[] infoParam) {

		super.msgBody = infoParam;
	}

	public FreqConfig_500() {
	}
}
