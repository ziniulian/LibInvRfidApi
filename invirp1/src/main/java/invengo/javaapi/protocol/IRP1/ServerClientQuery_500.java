package invengo.javaapi.protocol.IRP1;

/**
 * 500系列服务器/客户端状态查询
 *
 * @author dp732
 *
 */
public class ServerClientQuery_500 extends BaseMessage {

	public ServerClientQuery_500() {

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

		/**
		 * @return TCP/IP状态
		 */
		public byte getTcpIpStatus() {
			byte b = 0x00;
			if (buff != null && buff.length > 0) {
				b = buff[0];
			}
			return b;
		}

		private byte[] info = null;

		/**
		 * @return 获取信息
		 */
		public byte[] getInfo() {
			if (buff.length > 1) {
				info = new byte[buff.length - 1];
				System.arraycopy(buff, 1, info, 0, info.length);
			}
			return this.info;
		}

	}
}
