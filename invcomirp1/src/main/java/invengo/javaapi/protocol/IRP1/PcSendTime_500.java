package invengo.javaapi.protocol.IRP1;

/**
 * 读写器请求授时
 *
 * @author dp732
 *
 */
public class PcSendTime_500 extends BaseMessage {

	/**
	 * @param readerID 读写器ID，根据读写器发来的ID返回
	 * @param time 时隙
	 */
	public PcSendTime_500(byte[] readerID, byte[] time) {
		super.isReturn = false;
		super.msgBody = new byte[readerID.length + time.length];
		System.arraycopy(readerID, 0, super.msgBody, 0, readerID.length);
		System.arraycopy(time, 0, super.msgBody, readerID.length, time.length);
	}

	public PcSendTime_500() {
		super.isReturn = false;
	}

	public ReceivedInfo getReceivedMessage(){
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
			byte[] data = new byte[buff.length];
			System.arraycopy(buff, 0, data, 0, data.length);
			return data;
		}

	}
}
