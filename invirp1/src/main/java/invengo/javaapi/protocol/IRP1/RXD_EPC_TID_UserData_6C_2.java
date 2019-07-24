package invengo.javaapi.protocol.IRP1;

class RXD_EPC_TID_UserData_6C_2 extends BaseMessageNotification {

	//XC-IUT1501-01芯片测温标签初始值及步进值
	private int initValue = 30;
	private int stepValue = 20;

	public int getInitValue() {
		return initValue;
	}
	public void setInitValue(int initValue) {
		this.initValue = initValue;
	}
	public int getStepValue() {
		return stepValue;
	}
	public void setStepValue(int stepValue) {
		this.stepValue = stepValue;
	}

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
		//需要更新计算公式
		public double getTemperature(){
			double temperature = -100;

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
				if((this.epcLen + this.tidLen + this.udLen + 4) >= buff.length){
					return temperature;
				}
				if(temperatureLen == -1){
					temperatureLen = buff[5 + this.epcLen + this.tidLen + this.udLen] & 0xFF;
				}
				if((this.epcLen + this.tidLen + this.udLen + this.temperatureLen + 5) > buff.length){
					return temperature;
				}
				if(this.temperatureLen != 10){
					return temperature;
				}
				this.temperatureData = new byte[this.temperatureLen];
				System.arraycopy(buff, 6 + this.epcLen + this.tidLen + this.udLen, this.temperatureData, 0, this.temperatureLen);

				double C45 = temperatureData[0] << 8 | temperatureData[1] & 0xFF;
				double C60 = temperatureData[2] << 8 | temperatureData[3] & 0xFF;
				double C75 = temperatureData[4] << 8 | temperatureData[5] & 0xFF;
				double C90 = temperatureData[6] << 8 | temperatureData[7] & 0xFF;
				double cCurr = temperatureData[8] << 8 | temperatureData[9] & 0xFF;

				double C_45 = 0;
				double C_60 = 0;
				double C_75 = 0;
				int step = getStepValue();
				int initValue = getInitValue();

				if (cCurr <= C75) {
					C_45 = C45;
					C_60 = C60;
					C_75 = C75;
				} else {// cCurr > C75
					C_45 = C60;
					C_60 = C75;
					C_75 = C90;
					initValue += step;
				}

				double A = (step * (C_45 - 2 * C_60 + C_75))
						/ ((C_75 * C_75 * C_45 - C_75 * C_75 * C_60 + C_45
						* C_45 * C_60 - C_75 * C_45 * C_45 + C_75
						* C_60 * C_60 - C_45 * C_60 * C_60));
				double B = -((C_45 * C_45 + C_75 * C_75 - 2 * C_60 * C_60) * step)
						/ ((C_75 * C_75 * C_45 - C_75 * C_75 * C_60 + C_45
						* C_45 * C_60 - C_75 * C_45 * C_45 + C_75
						* C_60 * C_60 - C_45 * C_60 * C_60));
				double C = (initValue
						* (C_75 * C_75 * C_45 - C_75 * C_75 * C_60 + C_45
						* C_45 * C_60 - C_75 * C_45 * C_45 + C_75
						* C_60 * C_60 - C_45 * C_60 * C_60) + step
						* (2 * C_45 * C_45 * C_60 - C_45 * C_45 * C_75 + C_45
						* C_75 * C_75 - 2 * C_45 * C_60 * C_60))
						/ ((C_75 * C_75 * C_45 - C_75 * C_75 * C_60 + C_45
						* C_45 * C_60 - C_75 * C_45 * C_45 + C_75
						* C_60 * C_60 - C_45 * C_60 * C_60));

				//				if(a == -1 || b == -1 || c == -1){
				//					return temperature;
				//				}
				temperature = (A * cCurr * cCurr + B * cCurr + C);
			}
			return temperature;
		}
	}
}
