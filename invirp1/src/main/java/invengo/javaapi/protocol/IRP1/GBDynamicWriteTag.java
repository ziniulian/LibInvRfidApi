package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.GBMemoryBank;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 国标动态写指令
 */
public class GBDynamicWriteTag extends BaseMessage implements IEventHandle {

	public GBDynamicWriteTag() {
		// TODO Auto-generated constructor stub
	}

	private GBMemoryBank bank;
	private int executeFlag = -1;
	private static final int MAX_LENGTH = 0x20;
	private int lastHeadAddress = 0;
	private int lastLength = 0x20;
	private static final String DEFAULT_PASSWORD = "00000000";

	private int antenna;
	private int headAddress;
	private int length;
	private byte[] data;
	private String password;

	/**该指令用于动态写指定标签指定区域指定首地址指定长度的数据,其中指定长度为数据长度,单位为字<br>
	 * <p>
	 * 1.写指令.data.length>0x20时需要分段写入,必须先设置匹配数据和匹配区域参数,需要指定写入的数据data.<br>
	 * <p>
	 * 用法:<br>
	 * 写指令:1)length>0x20时<br>
	 GBDynamicWriteTag message = new GBDynamicWriteTag(antenna, password, bank, headAddress, length, data);<br>
	 message.enableSelectTag(tagId, matchingBank);<br>
	 reader.send(message);<br>
	 <p>2)length<=0x20时<br>
	 GBSelectTag selectTagMessage = new GBSelectTag(bank, target, rule, headAddress, data);<br>
	 if(reader.send(selectTagMessage)){<br>
	 GBDynamicWriteTag message = new GBDynamicWriteTag(antenna, password, bank, headAddress, length, data);<br>
	 reader.send(message);}<br>
	 <p>
	 *
	 * @param antenna	天线号
	 * @param password	访问密码
	 * @param bank	写入区域
	 * @param headAddress	写入首地址,单位为字
	 * @param data	写入数据
	 */
	public GBDynamicWriteTag(int antenna, String password, GBMemoryBank bank, int headAddress, byte[] data) {
		super.isReturn = false;
		this.antenna = antenna;
		this.password = password;
		this.bank = bank;
		this.headAddress = headAddress;
		this.length = data.length / 2;
		this.data = data;

		//		if(bank == GBMemoryBank.GBEPCMemory || bank == GBMemoryBank.GBReservedMemory){//写/擦除编码区、保留区访问密码为0
		//			this.password = DEFAULT_PASSWORD;
		//		}

		lastHeadAddress = this.headAddress;
		lastLength = this.length;
		byte[] dataByte = null;
		if(this.length > MAX_LENGTH){//length > 0x20
			executeFlag = 1;
			int srcPos = 0;
			if((this.length % MAX_LENGTH) == 0){
				lastHeadAddress = (this.headAddress + (this.length - MAX_LENGTH)) & 0xFF;
				lastLength = MAX_LENGTH;
				srcPos = (this.length - MAX_LENGTH) * 2;//因为双字节，所以要乘于2
			}else{
				lastHeadAddress = (this.headAddress + (this.length - (this.length % MAX_LENGTH))) & 0xFF;
				lastLength = (this.length % MAX_LENGTH);
				srcPos = (this.length - (this.length % MAX_LENGTH)) * 2;//因为双字节，所以要乘于2
			}
			dataByte = new byte[lastLength * 2];
			System.arraycopy(this.data, srcPos, dataByte, 0, dataByte.length);

			super.onExecuting.add(this);
		}else{//length <= 0x20
			dataByte = this.data;
		}
		byte[] passwordByte = Util.convertHexStringToByteArray(this.password);
		super.msgBody = new byte[5 + passwordByte.length + dataByte.length];

		super.msgBody[0] = (byte) this.antenna;
		System.arraycopy(passwordByte, 0, super.msgBody, 1, passwordByte.length);

		super.msgBody[5] = this.bank.getValue();
		super.msgBody[6] = (byte) (this.lastHeadAddress >> 8);
		super.msgBody[7] = (byte) (this.lastHeadAddress & 0xFF);
		super.msgBody[8] = (byte) this.lastLength;

		System.arraycopy(dataByte, 0, super.msgBody, 9, dataByte.length);
	}

	/**
	 * 启用分类指令,target,rule,selectHeadAddress默认为0
	 *
	 * @param tagId
	 * @param matchingBank
	 */
	public void enableSelectTag(byte[] tagId, GBMemoryBank matchingBank){
		super.tagID = tagId;
		super.matchingBank = matchingBank;
	}

	private int target = 0;
	private int rule = 0;
	private int selectHeadAddress = 0;
	/**
	 * 启用分类指令,target,rule,selectHeadAddress自定义
	 *
	 * @param tagId
	 * @param matchingBank
	 * @param target
	 * @param rule
	 * @param selectHeadAddress
	 */
	public void enableSelectTag(byte[] tagId, GBMemoryBank matchingBank, int target, int rule, int selectHeadAddress){
		super.tagID = tagId;
		super.matchingBank = matchingBank;
		this.target = target;
		this.rule = rule;
		this.selectHeadAddress = selectHeadAddress;
	}

	/**
	 * 禁用分类指令
	 */
	public void disableSelectTag(){
		super.tagID = null;
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		//do nothing
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		if(executeFlag == 1){
			writeTag_OnExecuting(sender, e);
			selectTag(sender, e);
		}
	}

	private void writeTag_OnExecuting(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;

		int loop = this.length / MAX_LENGTH;
		if(loop * MAX_LENGTH == this.length){
			loop -= 1;
		}

		for(int i = 0; i < loop; i++){
			selectTag(sender, e);//选择成功
			int currentHeadAddress = (this.headAddress + (i * MAX_LENGTH)) & 0xFF;

			byte[] tempData = new byte[MAX_LENGTH * 2];
			System.arraycopy(this.data, i * MAX_LENGTH * 2, tempData, 0, tempData.length);

			GBDynamicWriteTag currentMessage = new GBDynamicWriteTag(
					this.antenna, this.password, this.bank, currentHeadAddress, tempData);
			if(reader.send(currentMessage)){

			}else{
				int statusCode = currentMessage.getStatusCode() & 0xFF;
				if(statusCode != 0){//选择指令失败
					currentMessage.setStatusCode(statusCode);
					throw new RuntimeException("Select tag Failure!");
				}
				break;
			}
		}
	}

	@Override
	protected void selectTag(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;
		if(null != super.tagID){
			if(tagID.length > 32){
				byte[] temp = new byte[32];
				System.arraycopy(tagID, 0, temp, 0, 32);
				tagID = temp;
			}

			GBSelectTag message = new GBSelectTag(super.matchingBank, this.target, this.rule, this.selectHeadAddress, tagID);
			boolean success = reader.send(message);
			if(!success){
				int statusCode = message.getStatusCode() & 0xFF;
				if(statusCode != 0){//选择指令失败
					message.setStatusCode(statusCode);
					throw new RuntimeException("Select tag Failure!");
				}
			}
		}
	}
}
