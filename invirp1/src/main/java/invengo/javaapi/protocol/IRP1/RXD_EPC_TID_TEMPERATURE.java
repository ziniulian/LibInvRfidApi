package invengo.javaapi.protocol.IRP1;

/**
 * 解析测温标签返回的数据
 */
//配合铁路产品部门新增C1指令解析类
public class RXD_EPC_TID_TEMPERATURE extends BaseMessageNotification {

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo{

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte getAntenna() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;
		}

		private int epcLen = -1;
		private byte[] epc = null;

		public byte[] getEPC() {
			if (buff != null && buff.length >= 2) {
				if (epcLen == -1) {
					epcLen = buff[1] & 0xFF;
					if (epcLen + 1 > buff.length){
						return null;
					}
				}
				// 数据内容
				epc = new byte[epcLen];
				System.arraycopy(buff, 2, epc, 0, epcLen);
			}
			return this.epc;
		}

		private int tidLen = -1;
		private byte[] tid = null;

		public byte[] getTID() {
			if (buff != null && buff.length >= 3) {

				if (epcLen == -1) {
					epcLen = buff[1] & 0xFF;
				}
				if (tidLen == -1) {
					tidLen = buff[2 + epcLen] & 0xFF;
				}
				if (epcLen + tidLen + 2 > buff.length) {
					return null;
				}
				tid = new byte[tidLen];
				System.arraycopy(buff, 3 + epcLen, tid, 0, tidLen);
			}
			return this.tid;
		}

		private int temperatureLen = 2;//温度数据长度固定为2,在返回的数据中不包含温度长度这个字节
		private byte[] temperatureData = null;
		public String getTemperature(){
			String temperature = "";

			if(buff != null && buff.length >= 5){
				if(epcLen == -1){
					epcLen = buff[1] & 0xFF;
				}
				if(tidLen == -1){
					tidLen = buff[2 + this.epcLen] & 0xFF;
				}
				if((this.epcLen + this.tidLen + this.temperatureLen + 3) > buff.length){
					return temperature;
				}
				this.temperatureData = new byte[this.temperatureLen];//温度数据长度为2
				System.arraycopy(buff, 3 + this.epcLen + this.tidLen, this.temperatureData, 0, this.temperatureLen);

				int integer = this.temperatureData[0] & 0xFF;
				int decimal = this.temperatureData[1] & 0xFF;

				temperature = String.valueOf(integer) + "." + String.valueOf(decimal);
			}
			return temperature;
		}
	}
}
