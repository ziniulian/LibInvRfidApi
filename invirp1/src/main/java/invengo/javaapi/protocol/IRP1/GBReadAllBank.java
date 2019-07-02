package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.GBMemoryBank;
import invengo.javaapi.handle.EventArgs;

/**
 * 国标全区域通用读
 */
public class GBReadAllBank extends BaseMessage {

	private Reader currentReader;

	public GBReadAllBank() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param antenna	天线端口号
	 * @param tidlength	TID长度
	 * @param tidPwd	TID访问密码
	 * @param epclength	EPC长度
	 * @param userdatabank	用户数据区子区
	 * @param userdataptr	用户数据区起始地址
	 * @param userdatalen	用户数据区长度
	 * @param userdataPwd	用户数据子区密码
	 */
	public GBReadAllBank(byte antenna, byte tidlength, byte[] tidPwd,
						 byte epclength, GBMemoryBank userdatabank, byte userdataptr,
						 byte userdatalen, byte[] userdataPwd) {
		super.isReturn = false;

		super.msgBody = new byte[16];

		int i = 0;
		super.msgBody[i] = antenna;//天线端口
		i += 1;
		super.msgBody[i] = 0x01;//操作类型默认为0x01
		i += 1;
		super.msgBody[i] = epclength;//EPC长度
		i += 1;

		//TID访问密码
		System.arraycopy(tidPwd, 0, super.msgBody, i, tidPwd.length);
		i += tidPwd.length;
		super.msgBody[i] = tidlength;//TID长度
		i += 1;

		super.msgBody[i] = userdatabank.getValue();//用户数据区子区
		i += 1;
		//用户数据子区密码
		System.arraycopy(userdataPwd, 0, super.msgBody, i, userdataPwd.length);
		i += userdataPwd.length;

		//用户数据区起始地址
		super.msgBody[i] = (byte) (userdataptr >> 8);
		i += 1;
		super.msgBody[i] = (byte) (userdataptr & 0xFF);
		i += 1;
		super.msgBody[i] = userdatalen;//用户数据区长度
	}

	public void setCurrentReader(Reader currentReader) {
		this.currentReader = currentReader;
	}

	public ReadAllBankReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(data == null){
			return null;
		}
		return new ReadAllBankReceivedInfo(data, this.currentReader);
	}

	public class ReadAllBankReceivedInfo extends invengo.javaapi.core.ReceivedInfo{

		private Reader currentReader;
		public ReadAllBankReceivedInfo(byte[] buff, Reader currentReader) {
			super(buff);
			this.currentReader = currentReader;
		}

		public byte getAntenna(){
			if(null != buff && buff.length > 1){
				return buff[0];
			}
			return 0x00;
		}

		private int epcLen = -1;
		private byte[] epcData = null;
		public byte[] getGBEpcData(){
			if(null != buff && buff.length >= 2){
				if (epcLen == -1) {
					epcLen = buff[1] & 0xFF;
					if (epcLen + 1 > buff.length){
						return null;
					}
				}
				// 数据内容
				epcData = new byte[epcLen];
				System.arraycopy(buff, 2, epcData, 0, epcLen);
			}
			return epcData;
		}

		private int tidLen = -1;
		private byte[] tidData = null;
		public byte[] getGBTidData(){
			if(null != buff && buff.length >= 3){
				if (epcLen == -1) {
					epcLen = buff[1] & 0xFF;
				}
				if (tidLen == -1) {
					tidLen = buff[2 + epcLen] & 0xFF;
				}
				if (epcLen + tidLen + 2 > buff.length) {
					return null;
				}
				tidData = new byte[tidLen];
				System.arraycopy(buff, 3 + epcLen, tidData, 0, tidLen);
			}
			return tidData;
		}

		private int userdataLen = -1;
		private byte[] userdata = null;
		public byte[] getGBUserdata(){
			if(null != buff && buff.length >= 4){
				if (epcLen == -1) {
					epcLen = buff[1] & 0xFF;
				}
				if (tidLen == -1) {
					tidLen = buff[2 + epcLen] & 0xFF;
				}
				if(userdataLen == -1){
					userdataLen = buff[3 + epcLen + tidLen];
				}
				if (epcLen + tidLen + userdataLen + 3 > buff.length) {
					return null;
				}
				userdata = new byte[userdataLen];
				System.arraycopy(buff, 4 + epcLen + tidLen, userdata, 0, userdataLen);

			}
			return userdata;
		}

		private byte[] utcData = null;
		public byte[] getUTC(){
			if(null != currentReader){
				if(currentReader.isUtcEnable){
					if(null != buff && buff.length >= 12){
						utcData = new byte[8];
						System.arraycopy(buff, (buff.length - 8), utcData, 0, 8);
					}
				}
			}
			return utcData;
		}

	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		//do nothing
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		this.currentReader = (Reader) sender;
	}

}
