package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.GBMemoryBank;
import invengo.javaapi.core.Util;

/**
 * 国标擦除指令
 */
public class GBEraseTag extends BaseMessage {

	public GBEraseTag() {
		// TODO Auto-generated constructor stub
	}

	private GBMemoryBank bank;

	private int antenna;
	private int headAddress;
	private int length;
	private String data;
	private String password;

	/**该指令用于擦除指定标签指定区域指定首地址指定长度的数据<br>
	 * <p>
	 * 1.擦除指令.data默认为空.
	 * <p>
	 * 用法:<br>
	 <p>
	 擦除指令<br>
	 GBSelectTag selectTagMessage = new GBSelectTag(bank, headAddress, length, data, target, rule);<br>
	 if(reader.send(selectTagMessage)){<br>
	 GBEraseTag message = new GBEraseTag(antenna, password, bank, headAddress, length);<br>
	 reader.send(message);}<br>
	 *
	 * @param antenna	天线号
	 * @param password	访问密码
	 * @param bank	擦除区域
	 * @param headAddress	擦除首地址
	 * @param length	擦除长度
	 */
	public GBEraseTag(int antenna, String password, GBMemoryBank bank, int headAddress, int length) {
		this.antenna = antenna;
		this.password = password;
		this.bank = bank;
		this.headAddress = headAddress;
		this.length = length;

		this.data = "";

		byte[] passwordByte = Util.convertHexStringToByteArray(this.password);
		byte[] dataByte = Util.convertHexStringToByteArray(this.data);

		super.msgBody = new byte[5 + passwordByte.length + dataByte.length];

		super.msgBody[0] = (byte) this.antenna;

		System.arraycopy(passwordByte, 0, super.msgBody, 1, passwordByte.length);

		super.msgBody[5] = this.bank.getValue();
		super.msgBody[6] = (byte) (this.headAddress >> 8);
		super.msgBody[7] = (byte) (this.headAddress & 0xFF);
		super.msgBody[8] = (byte) this.length;

		System.arraycopy(dataByte, 0, super.msgBody, 9, dataByte.length);
	}

}
