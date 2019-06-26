package invengo.javaapi.protocol.IRP1;

/**
 * 读写器蜂鸣
 *
 * @author dp732
 *
 */
public class ReaderBeep extends BaseMessage {

	/**
	 * @param enabled 是否启用
	 * @param beepTime 蜂鸣时间（beepTime * 10ms）,0表示一直蜂鸣
	 */
	public ReaderBeep(boolean enabled, int beepTime) {
		super.msgBody = new byte[3];
		super.msgBody[0] = (byte) ((enabled) ? 0x01 : 0x00);
		super.msgBody[1] = (byte) (beepTime >> 8);
		super.msgBody[2] = (byte) (beepTime & 0xFF);
	}

	public ReaderBeep(){

	}
}
