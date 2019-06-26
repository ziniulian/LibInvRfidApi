package invengo.javaapi.protocol.IRP1;

/**
 * 客户端模式服务器IP查询
 *
 * @author dp732
 *
 */
public class PcIpsQuery_500 extends BaseMessage {

	public PcIpsQuery_500() {

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

		byte serverCount;

		/**
		 * @return 服务器个数
		 */
		public byte getServerCount() {
			if (buff.length >= 1) {
				this.serverCount = buff[0];
			}
			return this.serverCount;
		}

		String[] ipsInfo = null;

		/**
		 * @return IP信息
		 */
		public String[] getIpsInfo() {
			if (buff.length >= 1) {
				ipsInfo = new String[(buff.length - 1) / 4];
				for (int i = 0; i < ipsInfo.length; i++) {
					ipsInfo[i] = String.valueOf(buff[i * 4 + 1] & 0xFF) + "."
							+ String.valueOf(buff[i * 4 + 2] & 0xFF) + "."
							+ String.valueOf(buff[i * 4 + 3] & 0xFF) + "."
							+ String.valueOf(buff[i * 4 + 4] & 0xFF);
				}
			}
			return this.ipsInfo;
		}
	}
}
