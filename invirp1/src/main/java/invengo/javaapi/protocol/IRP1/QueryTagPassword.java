package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.protocol.receivedInfo.QueryTagPasswordReceivedInfo;

/**
 * 查询标签密码
 */
public class QueryTagPassword extends BaseMessage {

	public QueryTagPassword() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param antenna		天线号
	 * @param specialCode	特别码
	 * @param address		数据地址
	 */
	public QueryTagPassword(byte antenna, byte[] specialCode, byte[] address){
		super.msgBody = new byte[1 + specialCode.length + address.length];

		int i = 0;
		super.msgBody[i] = antenna;
		i += 1;

		System.arraycopy(specialCode, 0, super.msgBody, i, specialCode.length);
		i += specialCode.length;

		System.arraycopy(address, 0, super.msgBody, i, address.length);
	}

	public QueryTagPasswordReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new QueryTagPasswordReceivedInfo(data);
	}
}
