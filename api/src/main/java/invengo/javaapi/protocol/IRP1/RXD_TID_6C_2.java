package invengo.javaapi.protocol.IRP1;

class RXD_TID_6C_2 extends BaseMessage {

	/**
	 * @param readerID
	 *            读写器ID
	 * @param dataNO
	 *            数据序列号
	 */
	public RXD_TID_6C_2(byte[] readerID, byte[] dataNO) {
		super.isReturn = false;
		super.msgBody = new byte[readerID.length + dataNO.length];
		System.arraycopy(readerID, 0, super.msgBody, 0, readerID.length);
		System.arraycopy(dataNO, 0, super.msgBody, readerID.length,
				dataNO.length);
	}

	public RXD_TID_6C_2(){

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

		public byte[] getReaderID() {
			if (buff != null && buff.length > 7) {
				byte[] rid = new byte[8];
				System.arraycopy(buff, 0, rid, 0, 8);
				return rid;
			}
			return null;
		}

		public byte[] getDataNO() {
			if (buff != null && buff.length > 9) {
				byte[] dn = new byte[2];
				System.arraycopy(buff, 8, dn, 0, 2);
				return dn;
			}
			return null;
		}

		private byte[] tid = null;

		public byte[] getTID() {
			if (buff != null && buff.length > 10) {
				tid = new byte[buff.length - 15];
				System.arraycopy(buff, 10, tid, 0, tid.length);
			}
			return tid;
		}

		public byte getAntenna() {
			if (buff != null && buff.length >= 10) {
				return buff[buff.length - 5];
			}
			return 0x00;
		}

		public byte[] getTime() {
			if (buff != null && buff.length > 10) {
				byte[] time = new byte[4];
				System.arraycopy(buff, buff.length - 4, time, 0, 4);
				return time;
			}
			return null;
		}

	}
}
