package invengo.javaapi.protocol.IRP1;

/**
 * EPC匹配过滤指令
 *
 * @author yxx981
 *
 */
public class EpcFilter_6C extends BaseMessage {

	/**
	 *
	 * @param opType
	 *            类型
	 * @param epcData
	 *            匹配EPC码
	 * @param epcMask
	 *            要匹配掩码数据
	 */
	public EpcFilter_6C(byte opType, byte[] epcData, byte[] epcMask) {
		if (opType == (byte) 0) {
			super.msgBody = new byte[1 + epcData.length + epcMask.length];
			super.msgBody[0] = opType;
			System.arraycopy(epcData, 0, msgBody, 1, epcData.length);
			System.arraycopy(epcMask, 0, msgBody, epcData.length + 1,
					epcMask.length);
		} else {
			super.msgBody = new byte[] { opType };
		}
	}

	public EpcFilter_6C() {

	}
}
