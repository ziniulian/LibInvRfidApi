package invengo.javaapi.protocol.IRP1;

/**
 * 500系列查询DHCP功能
 *
 * @author dp732
 *
 */
public class QueryDhcp_500 extends BaseMessage {

	public QueryDhcp_500() {

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

		byte infoParam;

		public byte getInfoParam() {
			if (buff.length >= 1) {
				this.infoParam = buff[0];
			}
			return this.infoParam;
		}
	}
}
