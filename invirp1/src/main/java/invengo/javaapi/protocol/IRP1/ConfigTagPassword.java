package invengo.javaapi.protocol.IRP1;

public class ConfigTagPassword extends BaseMessage {

	public ConfigTagPassword() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param antenna		天线号
	 * @param specialCode	特别码
	 * @param address		数据地址
	 * @param tagData		标签数据
	 */
	public ConfigTagPassword(byte antenna, byte[] specialCode, byte[] address, byte[] tagData){
		super.msgBody = new byte[1 + specialCode.length + address.length + tagData.length];

		int i = 0;
		super.msgBody[i] = antenna;
		i += 1;

		System.arraycopy(specialCode, 0, super.msgBody, i, specialCode.length);
		i += specialCode.length;

		System.arraycopy(address, 0, super.msgBody, i, address.length);
		i += address.length;

		System.arraycopy(tagData, 0, super.msgBody, i, tagData.length);
	}

}
