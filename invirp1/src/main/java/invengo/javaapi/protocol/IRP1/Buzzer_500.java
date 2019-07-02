package invengo.javaapi.protocol.IRP1;

/**
 * 500系列蜂鸣器控制指令
 *
 * @author dp732
 *
 */
public class Buzzer_500 extends BaseMessage {

	/**
	 * @param infoType
	 *            蜂鸣器控制信息类型
	 */
	public Buzzer_500(byte infoType) {
		super.msgBody = new byte[] { infoType };
	}

	public Buzzer_500() {
	}
}
