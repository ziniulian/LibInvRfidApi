package invengo.javaapi.protocol.IRP1;

/**
 * 800系列开功放
 *
 * @author dp732
 *
 */
class PowerOn_800 extends BaseMessage {

	public PowerOn_800(byte antenna) {
		super.msgBody = new byte[] { antenna };
	}

	public PowerOn_800(){}
}
