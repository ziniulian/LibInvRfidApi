package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.GBMemoryBank;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.receivedInfo.GBAccessReadReceivedInfo;

/**
 * 国标标签访问读指令
 *
 */
public class GBAccessReadTag extends BaseMessage implements IEventHandle{

	private GBReadMemoryBank rmb;
	private GBMemoryBank bank;
	private int executeFlag = -1;
	private static final int MAX_LENGTH = 0x20;
	private int lastHeadAddress = 0;
	private int lastLength = 0x20;
	private List<Byte> userDataCache = new ArrayList<Byte>();

	private Reader currentReader;

	/**
	 * 1.用于单次或多次访问读标签信息区、编码区数据(默认长度为0x20个字)<br>
	 * rmb设置为TID_GB_Access, EPC_GB_Access,可以通过setXXX()方法对天线端口、操作类型、访问密码、
	 * 访问区域首地址、读取长度等五个属性进行设置;<br>
	 * <p>
	 * 2.用于多次快速访问指定用户子区数据(读取长度默认为0x20个字)<br>
	 * rmb设置为Sub_UserData_GB_Access,matchingBank设置为用户子区编号(GBUser1Memory...GBUser15Memory),可以通过setXXX()方法对天线端口、访问密码、
	 * 访问区域首地址、读取长度等四个属性进行设置,其余方法禁止设置;<br>
	 * <p>
	 *
	 示例：
	 <p>GBAccessReadTag message = new GBAccessReadTag(rmb,matchingBank);<br>
	 message.setOperationType(operationType);<br>
	 .<br>
	 .<br>
	 .<br>
	 注:根据上面所描述选择setXXX()方法进行设置<br>
	 reader.send(message);<br>

	 <p>通过回调函数进行数据接收

	 public class XXX implements IMessageNotificationReceivedHandle{<br>
	 .<br>
	 .<br>
	 .<br>
	 <p>
	 public void messageNotificationReceivedHandle(BaseReader reader,IMessageNotification msg){<br>
	 if(msg instanceof GBAccessReadTag){<br>
	 //do something.<br>
	 }<br>
	 }<br>
	 <p>
	 .<br>
	 .<br>
	 .<br>
	 }
	 *
	 * @param rmb	读取区域-标签信息区、编码区、用户子区
	 * @param matchingBank	用户子区编号,当rmb为Sub_UserData_GB_Access时有效
	 */
	public GBAccessReadTag(GBReadMemoryBank rmb,GBMemoryBank matchingBank){
		this.rmb = rmb;
		super.isReturn = false;

		switch (rmb) {
			case EPC_GB_Access:
				super.msgBody = new byte[] {(byte) 0x81, 0x01, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x20};
				break;
			case TID_GB_Access:
				super.msgBody = new byte[] {(byte) 0x81, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20 };
				break;
			case Sub_UserData_GB_Access://快速读用户子区数据，数据长度为0x20
				super.msgBody = new byte[] {(byte) 0x81, 0x01, 0x00, 0x00, 0x00, 0x00, matchingBank.getValue(), 0x00, 0x00, 0x20 };
				break;
		}
	}

	/**
	 * 用于自定义选择单次读用户子区.通过此构造函数实现访问读用户子区数据时,
	 * 当读取长度大于0x20个字时必须先设置匹配数据和匹配区域参数；当读取长度小于等于0x20个字时必须先发送分类指令<br>

	 <p>示例：
	 <p>length>0x20时<br>
	 GBAccessReadTag message = new GBAccessReadTag(antenna, password, bank, headAddress, length);<br>
	 message.enableSelectTag(tagId, matchingBank);<br>
	 reader.send(message);<br>
	 <p>length<=0x20时<br>
	 GBSelectTag selectTagMessage = new GBSelectTag(bank, target, rule, headAddress, data);<br>
	 if(reader.send(selectTagMessage)){<br>
	 GBAccessReadTag message = new GBAccessReadTag(antenna, password, bank, headAddress, length);<br>
	 reader.send(message);}<br>

	 <p>获取返回数据:<br>
	 if(reader.send(message){
	 byte[] tagData = message.getReceivedMessage().getTagData();
	 }
	 *
	 * @param antenna	天线端口号
	 * @param password	访问密码
	 * @param bank	访问区域,值为GBUser1Memory...GBUser16Memory;若设置成GBEPCMemory或GBTidMemory则默认设置为GBUser1Memory
	 * @param headAddress	访问区域首地址
	 * @param length	读取长度
	 *
	 */
	public GBAccessReadTag(byte antenna, String password, GBMemoryBank bank, int headAddress, int length) {
		this.antenna = antenna;
		this.rmb = GBReadMemoryBank.Sub_UserData_GB_Access;
		this.defaultAccessPassword = password;
		if(bank == GBMemoryBank.GBEPCMemory || bank == GBMemoryBank.GBTidMemory){
			this.bank = GBMemoryBank.GBUser1Memory;
		}else{
			this.bank = bank;
		}
		this.headAddress = headAddress;
		this.length = length;

		lastHeadAddress = headAddress;
		lastLength = length;
		if(length > MAX_LENGTH){
			executeFlag = 1;
			if(length % MAX_LENGTH == 0){
				lastHeadAddress = (headAddress + (length - MAX_LENGTH)) & 0xFF;
				lastLength = MAX_LENGTH;
			}else{
				lastHeadAddress = (headAddress + (length - (length % MAX_LENGTH))) & 0xFF;
				lastLength = length % MAX_LENGTH;
			}
			super.onExecuting.add(this);
		}

		super.msgBody = new byte[10];
		//天线端口号
		msgBody[0] = this.antenna;

		//操作类型
		msgBody[1] = 0x00;

		//访问密码
		byte[] passwordData = Util.convertHexStringToByteArray(password);
		System.arraycopy(passwordData, 0, super.msgBody, 2, passwordData.length);

		//访问区域
		super.msgBody[6] = bank.getValue();

		//访问区域首地址-最后一次
		byte[] address = new byte[2];
		address[0] = (byte) (lastHeadAddress >> 8);
		address[1] = (byte) (lastHeadAddress & 0xFF);
		System.arraycopy(address, 0, super.msgBody, 7, address.length);

		//读取长度-最后一次
		super.msgBody[9] = (byte) lastLength;
	}

	public GBAccessReadTag(){

	}

	private byte antenna;
	public byte getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna 天线端口号
	 */
	public void setAntenna(byte antenna) {
		this.antenna = antenna;
		super.msgBody[0] = antenna;
	}

	private int operationType = 0;
	public int getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType	操作类型
	 */
	public void setOperationType(int operationType) {
		this.operationType = operationType;
		switch (this.rmb) {
			case EPC_GB_Access:
			case TID_GB_Access:
				super.msgBody[1] = (byte) operationType;
				break;
			default:
				break;
		}
	}

	private String defaultAccessPassword = "000000";
	public String getDefaultAccessPassword() {
		return defaultAccessPassword;
	}

	/**
	 * @param defaultAccessPassword	访问密码
	 */
	public void setDefaultAccessPassword(String defaultAccessPassword) {
		this.defaultAccessPassword = defaultAccessPassword;
		byte[] password = Util.convertHexStringToByteArray(defaultAccessPassword);
		System.arraycopy(password, 0, super.msgBody, 2, password.length);
	}

	private int headAddress = 0;
	public int getHeadAddress() {
		return headAddress;
	}

	/**
	 * @param headAddress	访问区域首地址
	 */
	public void setHeadAddress(int headAddress) {
		this.headAddress = headAddress;
		byte[] address = new byte[2];
		address[0] = (byte) (headAddress >> 8);
		address[1] = (byte) (headAddress & 0xFF);
		System.arraycopy(address, 0, super.msgBody, 7, address.length);
	}

	private int length = 0x20;
	public int getLength() {
		return length;
	}

	/**
	 * 多次读取用户子区数据时设置读取长度最大不能超过0x20,超过0x20则默认长度为0x20.
	 * 单位为字
	 * @param length	读取长度
	 */
	public void setLength(int length) {
		this.length = length;
		switch (this.rmb) {
			case EPC_GB_Access:
			case TID_GB_Access:
				super.msgBody[9] = (byte) length;
				break;
			case Sub_UserData_GB_Access:
				if(length > 0x20){
					this.length = 0x20;
				}else {
					this.length = length;
				}
				super.msgBody[9] = (byte) this.length;
				break;
			default:
				break;
		}
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

	public enum GBReadMemoryBank{
		TID_GB_Access, EPC_GB_Access, Sub_UserData_GB_Access
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		//do nothing
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		if(executeFlag == 1){
			readSubUserData_OnExecuting(sender, e);
			selectTag(sender, e);
		}
	}

	private void readSubUserData_OnExecuting(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;

		int loop = this.length / MAX_LENGTH;
		if(loop * MAX_LENGTH == this.length){//0x20倍数
			loop -= 1;
		}

		for(int i = 0; i < loop; i++){
			selectTag(sender, e);
			int currentHeadAddress = (this.headAddress + (i * MAX_LENGTH)) & 0xFF;

			GBAccessReadTag accessReadTag = new GBAccessReadTag(
					this.antenna, this.defaultAccessPassword, this.bank, currentHeadAddress, MAX_LENGTH);
			if(reader.send(accessReadTag)){
				//缓存此次读取的数据
				if(accessReadTag.getSingleReceivedMessage() != null){
					if(null != accessReadTag.getSingleReceivedMessage().getTagData()){
						for(byte data : accessReadTag.getSingleReceivedMessage().getTagData()){
							userDataCache.add(data);
						}
					}
				}

				/*
				 * 下列缓存数据方法会导致循环次数增多
				 *
				if(accessReadTag.getReceivedMessage() != null){
					if(null != accessReadTag.getReceivedMessage().getTagData()){
						for(byte data : accessReadTag.getSingleReceivedMessage().getTagData()){
							userDataCache = new ArrayList<Byte>();
							userDataCache.add(data);
						}
					}
				}
				 *
				 */
			}else{
				int statusCode = accessReadTag.getStatusCode() & 0xFF;
				if(statusCode != 0){//选择指令失败
					accessReadTag.setStatusCode(statusCode);
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

	public void setCurrentReader(Reader currentReader) {
		this.currentReader = currentReader;
	}

	private ReceivedInfo getSingleReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(data == null){
			return null;
		}
		return new ReceivedInfo(data, this.currentReader);
	}

	//获取最终的数据
	public GBAccessReadReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(data != null && data.length > 0){
			if(userDataCache.size() > 0){
				byte[] temp = new byte[data.length + userDataCache.size()];
				temp[0] = data[0];//天线端口号
				byte[] old = new byte[userDataCache.size()];
				for(int i = 0; i < userDataCache.size(); i++){
					old[i] = userDataCache.get(i);
				}

				System.arraycopy(old, 0, temp, 1, old.length);
				System.arraycopy(data, 1, temp, 1 + userDataCache.size(), data.length - 1);
				data = temp;
			}
		}else{
			return null;
		}
		return new GBAccessReadReceivedInfo(data, this.currentReader.isUtcEnable());
	}

	private class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo{

		private Reader currentReader;
		public ReceivedInfo(byte[] buff, Reader currentReader) {
			super(buff);
			this.currentReader = currentReader;
		}

		public byte getAntenna(){
			if(null != buff && buff.length > 1){
				return buff[0];
			}
			return 0x00;
		}

		private byte[] tagData = null;
		public byte[] getTagData(){
			if(null != currentReader){
				if(currentReader.isUtcEnable){//utc(8 bytes)
					if(null != buff && buff.length >= 10){
						tagData = new byte[buff.length - 9];
						System.arraycopy(buff, 1, tagData, 0, tagData.length);
					}
				}else {//no utc
					if(null != buff && buff.length >= 2){
						tagData = new byte[buff.length - 1];
						System.arraycopy(buff, 1, tagData, 0, tagData.length);
					}
				}
			}
			return tagData;
		}

		private byte[] utcData = null;
		public byte[] getUTC(){
			if(null != currentReader){
				if(currentReader.isUtcEnable){
					if(null != buff && buff.length >= 10){
						utcData = new byte[8];
						System.arraycopy(buff, (buff.length - 8), utcData, 0, 8);
					}
				}
			}
			return utcData;
		}

	}

}
