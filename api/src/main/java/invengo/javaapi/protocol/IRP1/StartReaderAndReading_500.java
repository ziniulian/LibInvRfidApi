package invengo.javaapi.protocol.IRP1;

/**
 * 500系列开机读卡模式设置
 *
 * @author dp732
 *
 */
public class StartReaderAndReading_500 extends BaseMessage {

	/**
	 * @param data
	 *            0x00:开机不读卡；0x01:开机读卡；
	 */
	public StartReaderAndReading_500(byte data) {
		// 指令内容
		super.msgBody = new byte[] { data };
	}

	public StartReaderAndReading_500() {
	}
}
