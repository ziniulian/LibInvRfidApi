package invengo.javaapi.protocol.IRP1;

/**
 * 固定TID长度设置/查询
 *
 * @author zxq943
 *
 */
public class FixedTidLengthConfig_6C extends BaseMessage {

	/**
	 * @param infoType 信息类型：0x00设置，0x01 查询
	 * @param infoParam 信息参数：读取固定长度TID的字个数（需大于0）
	 */
	public FixedTidLengthConfig_6C(byte infoType, byte infoParam)
	{
		//指令内容
		super.msgBody = new byte[] { infoType, infoParam };
	}

	public FixedTidLengthConfig_6C(){}

	public FixedTidLengthConfig6CReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null)
			return null;
		return new FixedTidLengthConfig6CReceivedInfo(data);
	}

	public class FixedTidLengthConfig6CReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		private static final long serialVersionUID = 234124222351941744L;

		public FixedTidLengthConfig6CReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte[] getQueryData()
		{
			byte[] data = new byte[buff.length - 1];
			System.arraycopy(buff, 1, data, 0, data.length);
			return data;
		}
	}
}