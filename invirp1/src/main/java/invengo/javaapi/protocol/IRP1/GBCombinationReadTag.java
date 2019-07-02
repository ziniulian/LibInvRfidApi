package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.Util;

/**
 * GB-组合读EPC & TID数据指令
 */
public class GBCombinationReadTag extends BaseMessage {

	private Reader currentReader;

	public GBCombinationReadTag() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param antenna	天线端口号
	 * @param operationType	操作类型
	 * @param tidPassword	tid区域访问密码
	 * @param tidLength	读取Tid长度
	 */
	public GBCombinationReadTag(byte antenna, int operationType, String tidPassword, int tidLength){
		super.isReturn = false;

		byte[] password = Util.convertHexStringToByteArray(tidPassword);
		super.msgBody = new byte[3 + password.length];

		super.msgBody[0] = antenna;
		super.msgBody[1] = (byte) operationType;

		System.arraycopy(password, 0, super.msgBody, 2, password.length);

		super.msgBody[2 + password.length] = (byte) tidLength;

	}

	public void setCurrentReader(Reader currentReader) {
		this.currentReader = currentReader;
	}

	public CombinationReadTagReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(data == null){
			return null;
		}
		return new CombinationReadTagReceivedInfo(data, this.currentReader);
	}

	public class CombinationReadTagReceivedInfo extends invengo.javaapi.core.ReceivedInfo{

		private Reader currentReader;
		public CombinationReadTagReceivedInfo(byte[] buff, Reader currentReader) {
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
