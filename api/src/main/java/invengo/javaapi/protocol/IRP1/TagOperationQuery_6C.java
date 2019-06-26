package invengo.javaapi.protocol.IRP1;

/**
 * 查询标签操作配置指令
 *
 * @author dp732
 *
 */
public class TagOperationQuery_6C extends BaseMessage {

	public TagOperationQuery_6C(byte parameter) {
		// 指令内容
		super.msgBody = new byte[2];
		super.msgBody[0] = parameter;
		super.msgBody[1] = (byte) 0;// 保留域默认为00H
	}

	public TagOperationQuery_6C(){}

	@Override
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

		public byte[] getQueryData() {
			return buff;
		}
	}
}
