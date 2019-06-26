package invengo.javaapi.protocol.IRP1;

/**
 * 重复标签按时间过滤指令
 *
 * @author dp732
 *
 */
public class FilterByTime_6C extends BaseMessage {

	/**
	 * @param opType
	 *            类型
	 * @param time
	 *            时间
	 */
	public FilterByTime_6C(byte opType, byte[] time) {
		if (opType == (byte) 0)// 00H：配置
		{
			super.msgBody = new byte[1 + time.length];
			super.msgBody[0] = opType;
			System.arraycopy(time, 0, msgBody, 1, time.length);
		} else if (opType == (byte) 1)// 01H：取消(后一项不需要)
		{
			super.msgBody = new byte[] { opType };
		}
	}

	public FilterByTime_6C() {
	}
}
