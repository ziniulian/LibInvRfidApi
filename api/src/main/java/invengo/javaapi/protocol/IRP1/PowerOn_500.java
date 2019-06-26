package invengo.javaapi.protocol.IRP1;

/**
 * 500系列开功放
 *
 * @author dp732
 *
 */
class PowerOn_500 extends BaseMessage {

	/**
	 * @param antenna 天线号
	 */
	public PowerOn_500(byte antenna) {
		super.msgBody = new byte[] { antenna };
	}

	public PowerOn_500() {
	}
}
