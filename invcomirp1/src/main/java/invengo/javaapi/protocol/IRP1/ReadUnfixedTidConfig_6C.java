package invengo.javaapi.protocol.IRP1;

/**
 * 读变长TID开关设置/查询
 *
 * @author zxq943
 *
 */
public class ReadUnfixedTidConfig_6C extends BaseMessage {

	/**
	 * @param infoType 信息类型：0x00设置，0x01查询
	 * @param infoParam 信息参数:0x00关闭读变长TID功能,0x01开启读变长TID功能
	 */
	public ReadUnfixedTidConfig_6C(byte infoType, byte infoParam)
	{
		//指令内容
		super.msgBody = new byte[] { infoType, infoParam };
	}

	public ReadUnfixedTidConfig_6C(){}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null)
			return null;
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
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