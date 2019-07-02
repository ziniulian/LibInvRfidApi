package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.Util;

class RXD_EPC_TID_UserData_6C_2 extends BaseMessageNotification {

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

		private int udLen = -1;
		private byte[] ud = null;

		public byte[] getUserData() {
			if (buff != null && buff.length >= 4) {
				if (epcLen == -1) {
					epcLen = buff[1] & 0xFF;
				}
				if (tidLen == -1) {
					tidLen = buff[2 + this.epcLen] & 0xFF;
				}
				if (udLen == -1) {
					udLen = buff[3 + epcLen + tidLen] & 0xFF;
				}
				if (epcLen + tidLen + udLen + 3 > buff.length) {
					return null;
				}
				// 数据内容
				this.ud = new byte[udLen];
				System.arraycopy(buff, 4 + epcLen + tidLen, ud, 0, udLen);
			}
			return this.ud;
		}

		private int temperatureLen = -1;
		private byte[] temperatureData = null;
		public double getTemperature(){
			double temperature = 0;

			if(buff != null && buff.length >= 6){
				if(epcLen == -1){
					epcLen = buff[1] & 0xFF;
				}
				if(tidLen == -1){
					tidLen = buff[2 + this.epcLen] & 0xFF;
				}
				if(udLen == -1){
					udLen = buff[3 + this.epcLen + this.tidLen] & 0xFF;
				}
				if(this.epcLen + this.tidLen + this.udLen + 4 >= buff.length){
					return temperature;
				}
				if(temperatureLen == -1){
					temperatureLen = buff[5 + this.epcLen + this.tidLen + this.udLen] & 0xFF;
				}
				if((this.epcLen + this.tidLen + this.udLen + this.temperatureLen + 5) > buff.length){
					return temperature;
				}
				if(this.temperatureLen != 14){
					return temperature;
				}
				this.temperatureData = new byte[this.temperatureLen];
				System.arraycopy(buff, 6 + this.epcLen + this.tidLen + this.udLen, this.temperatureData, 0, this.temperatureLen);

				byte[] aByte = new byte[4];
				System.arraycopy(temperatureData, 0, aByte, 0, 4);
				byte[] bByte = new byte[4];
				System.arraycopy(temperatureData, 4, bByte, 0, 4);
				byte[] cByte = new byte[4];
				System.arraycopy(temperatureData, 8, cByte, 0, 4);
				byte[] Tcount = new byte[2];
				System.arraycopy(temperatureData, 12, Tcount, 0, 2);

				double a = Util.convertByteToDouble(Util.convertByteArrayToHexString(aByte));
				double b = Util.convertByteToDouble(Util.convertByteArrayToHexString(bByte));
				double c = Util.convertByteToDouble(Util.convertByteArrayToHexString(cByte));
				short tcount = Short.parseShort(Util.convertByteArrayToHexString(Tcount), 16);

				double tidle = tcount / (30.595 + 0.0125);
				temperature = (a * tidle * tidle + b * tidle + c);
			}
			return temperature;
		}
	}
}
