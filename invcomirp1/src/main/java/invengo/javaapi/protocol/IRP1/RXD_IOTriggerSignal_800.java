package invengo.javaapi.protocol.IRP1;

/**
 * 读卡信号
 *
 * @author dp732
 *
 */
public class RXD_IOTriggerSignal_800 extends BaseMessageNotification {

	public RXD_IOTriggerSignal_800() {

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
		 * 获取输入端口
		 *
		 * @return
		 */
		public byte getGPIPort() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;
		}

		/**
		 * 获取信号
		 *
		 * @return
		 */
		public boolean isStart() {
			if (buff != null && buff.length >= 3) {
				return (buff[1] == 0x00);
			}
			return false;
		}

		/**
		 * 获取UTC时间
		 *
		 * @return
		 */
		public byte[] getUTCTime() {
			byte[] utc = new byte[8];
			if (buff != null && buff.length >= 10) {
				System.arraycopy(buff, 2, utc, 0, 8);
			}
			return utc;
		}

	}
}
