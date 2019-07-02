package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.TagOperationQuery6CReceivedInfo;

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
	public TagOperationQuery6CReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new TagOperationQuery6CReceivedInfo(data);
	}

}
