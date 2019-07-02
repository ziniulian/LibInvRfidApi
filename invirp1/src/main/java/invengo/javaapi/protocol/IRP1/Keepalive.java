package invengo.javaapi.protocol.IRP1;


/**
 * keepalive指令
 *
 * @author dp732
 *
 */
public class Keepalive extends BaseMessage {

	public Keepalive() {
	}

	public Keepalive(byte[] sequence, byte[] utc) {
		if (sequence == null || sequence.length != 4 || utc == null
				|| utc.length != 8) {
			throw new IllegalArgumentException();
		}
		super.isReturn = false;
		super.msgBody = new byte[12];

		System.arraycopy(sequence, 0, super.msgBody, 0, 4);
		System.arraycopy(utc, 0, super.msgBody, 4, 8);

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
		 * 获取间隔时间
		 *
		 * @return
		 */
		public byte[] getIntervalTime() {
			byte[] intervalTime = new byte[2];
			if (buff != null && buff.length >= 10) {
				System.arraycopy(buff, 0, intervalTime, 0, 2);
			}
			return intervalTime;
		}

		/**
		 * 获取序列号
		 *
		 * @return
		 */
		public byte[] getSequence() {
			byte[] sequence = new byte[4];
			if (buff != null && buff.length >= 6) {
				System.arraycopy(buff, 2, sequence, 0, 4);
			}
			return sequence;
		}

	}
}
