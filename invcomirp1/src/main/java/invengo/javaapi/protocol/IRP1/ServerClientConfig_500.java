package invengo.javaapi.protocol.IRP1;

public class ServerClientConfig_500 extends BaseMessage {

	/**
	 * @param type
	 *            服务器/客户端模式
	 * @param ports
	 *            两个端口号
	 */
	public ServerClientConfig_500(byte type, byte[] ports) {
		super.msgBody = new byte[1 + ports.length];
		super.msgBody[0] = type;
		System.arraycopy(ports, 0, super.msgBody, 1, ports.length);
	}

	public ServerClientConfig_500(){

	}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte getReaderType() {
			if (buff != null && buff.length > 0) {
				return buff[0];
			}
			return 0x00;
		}

	}
}
