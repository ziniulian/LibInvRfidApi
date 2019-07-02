package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.Util;

/**
 * 灭活标签指令
 */
public class GBInactivateTag extends BaseMessage {

	public GBInactivateTag() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 该指令用于标签灭活操作
	 *
	 * <p>
	 用法:<br>
	 GBSelectTag selectTagMessage = new GBSelectTag(bank, target, rule, headAddress, length, data);<br>
	 if(reader.send(selectTagMessage)){<br>
	 GBInactivateTag message = new GBInactivateTag(antenna, password);<br>
	 reader.send(message);}<br>
	 *
	 * @param antenna	天线端口
	 * @param password	灭活密码
	 */
	public GBInactivateTag(int antenna, String password) {
		byte[] passwordByte = Util.convertHexStringToByteArray(password);

		super.msgBody = new byte[1 + passwordByte.length];
		super.msgBody[0] = (byte) antenna;
		System.arraycopy(passwordByte, 0, super.msgBody, 1, passwordByte.length);
	}

}
