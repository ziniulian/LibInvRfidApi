package invengo.javaapi.protocol.IRP1;

/**
 * 读6C所有数据信息参数设置/查询
 *
 * @author dp732
 *
 */
public class ReadTagConfig_6C extends BaseMessage {

	/**
	 *
	 * 构造函数
	 *
	 * @param infoType
	 *            信息类型：0为设置，1为查询
	 * @param infoParam
	 *            信息参数：
	 *       	  第1个字节读EPC开关，0为不读取EPC码，1为读取EPC码
	 *            第2个字节读TID开关，0为不读取TID码，1为读取TID码
	 *            第3个字节读TID码的长度
	 *            第4个字节读用户数据开关，0为不读取用户数据，1为读取用户数据
	 *            第5个字节读用户数据区的长度
	 *
	 */
	public ReadTagConfig_6C(byte infoType, byte[] infoParam) {
		int len = 1;
		if (infoParam != null){
			len += infoParam.length;
		}
		super.msgBody = new byte[len];
		super.msgBody[0] = infoType;
		if (infoParam != null) {
			System.arraycopy(infoParam, 0, super.msgBody, 1, infoParam.length);
		}
	}

	public ReadTagConfig_6C(){}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		private byte infoType;
		private byte[] infoParam;

		public byte getInfoType() {
			if (buff != null && buff.length >= 1) {
				infoType = buff[0];
			}
			return this.infoType;
		}

		public byte[] getInfoParam() {
			if (buff != null && buff.length >= 3) {
				infoParam = new byte[buff.length - 1];
				System.arraycopy(buff, 1, infoParam, 0, infoParam.length);
			}
			return this.infoParam;
		}
	}
}
