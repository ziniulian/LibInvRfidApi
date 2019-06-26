package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.MemoryBank;

/**
 * 标签选择指令
 */

public class SelectTag_6C extends BaseMessage {

	/**
	 * @param memoryBank
	 *            匹配数据区
	 * @param ptr
	 *            匹配数据起始地址(EVB格式)
	 * @param matchBitLength
	 *            匹配比特数
	 * @param tagData
	 *            匹配数据
	 */
	public SelectTag_6C(MemoryBank memoryBank, int ptr, byte matchBitLength,
						byte[] tagData) {
		byte[] ptrArray = EVB.convertToEvb(ptr);
		super.msgBody = new byte[1 + tagData.length + ptrArray.length + 1];
		int p = 0;

		super.msgBody[0] = memoryBank.getValue();
		p += 1;

		System.arraycopy(ptrArray, 0, msgBody, p, ptrArray.length);
		p += ptrArray.length;

		msgBody[p] = matchBitLength;
		p++;

		System.arraycopy(tagData, 0, super.msgBody, p, tagData.length);
	}

	public SelectTag_6C() {
	}

	public static int select(BaseReader reader, byte[] tagID,
							 MemoryBank tagIDType) {
		if (tagID.length > 32) {
			byte[] t = new byte[32];
			System.arraycopy(tagID, 0, t, 0, 32);
			tagID = t;
		}
		SelectTag_6C msg = new SelectTag_6C(tagIDType, (byte) 0x00,
				(byte) (tagID.length * 8), tagID);
		reader.send(msg);
		return (msg.getStatusCode()) & 0xFF;
	}

}
