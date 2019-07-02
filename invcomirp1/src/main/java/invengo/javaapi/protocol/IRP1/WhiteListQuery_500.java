package invengo.javaapi.protocol.IRP1;

/**
 * 500系列白名单查询
 *
 * @author dp732
 *
 */
public class WhiteListQuery_500 extends BaseMessage {

	/**
	 * @param data
	 *            查询信息：00，00 =查询状态和记录条数； 其他=查询具体的条目内容（条目的序号不能大于总的条目数）
	 */
	public WhiteListQuery_500(byte[] data) {
		// 指令内容
		super.msgBody = data;
	}

	public WhiteListQuery_500() {
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

		private byte flag; // 白名单启用标志
		private byte[] count = null;// 白名单条目
		private byte[] content = null;// 白名单条目

		public byte getFlag() {
			if (buff != null && buff.length >= 1) {
				flag = buff[0];
			}
			return this.flag;
		}

		public byte[] getCount() {
			if (buff != null && buff.length >= 3) {
				this.count = new byte[2];
				System.arraycopy(buff, 1, count, 0, 2);
			}
			return this.count;
		}

		public byte[] getContent() {
			if (buff != null && buff.length >= 11) {
				this.content = new byte[8];
				System.arraycopy(buff, 3, content, 0, 8);
			}
			return this.content;
		}
	}
}
