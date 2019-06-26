package invengo.javaapi.protocol.IRP1;

/**
 * ID数据过滤指令
 *
 * @author dp732
 *
 */
public class IDFilter_6B extends BaseMessage {

	/**
	 *
	 * @param tagID
	 *            ID(8字节)
	 * @param tagMask
	 *            匹配参数(8字节
	 */
	public IDFilter_6B(byte[] tagID, byte[] tagMask) {
		super.msgBody = new byte[tagID.length + tagMask.length];
		System.arraycopy(tagID, 0, msgBody, 0, tagID.length);
		System.arraycopy(tagMask, 0, msgBody, tagID.length, tagMask.length);
	}

	public IDFilter_6B() {
	}
}
