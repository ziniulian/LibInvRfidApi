package invengo.javaapi.protocol.IRP1;

/**
 * 800系列IO输出操作
 *
 * @author dp732
 *
 */
public class Gpo_800 extends BaseMessage {

	/**
	 * @param ioPort
	 *            输出端口
	 * @param level
	 *            输出电平
	 */
	public Gpo_800(byte ioPort, byte level) {
		super.msgBody = new byte[] { ioPort, level };
	}

	public Gpo_800() {
	}
}
