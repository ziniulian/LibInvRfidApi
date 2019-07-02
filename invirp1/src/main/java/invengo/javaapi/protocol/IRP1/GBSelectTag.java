package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.GBMemoryBank;


/**
 * GB-分类指令
 *
 */
public class GBSelectTag extends BaseMessage {

	public GBSelectTag(){
		//do nothing
	}

	/**
	 * @param bank-匹配存储区
	 * 			0x01:标签信息区,02:编码区,03:安全区,0x30~3F:用户子区0~15
	 * @param target-匹配目标
	 * 			值为:0,1,2,3,4
	 * @param rule-匹配规则
	 * 			值为:0,1,2,3
	 * @param headAddress-匹配存储区起始地址
	 * @param data-匹配数据
	 * 			最大长度为32bytes
	 */
	public GBSelectTag(GBMemoryBank bank,int target, int rule, int headAddress, byte[] data){
		super.msgBody = new byte[6 + data.length];

		super.msgBody[0] = bank.getValue();
		super.msgBody[1] = (byte) target;
		super.msgBody[2] = (byte) rule;

		super.msgBody[3] = (byte) (headAddress >> 8);
		super.msgBody[4] = (byte) (headAddress & 0xFF);
		super.msgBody[5] = (byte) (data.length * 8);

		int selectDataLength = data.length;
		System.arraycopy(data, 0, super.msgBody, 6, selectDataLength);
	}

}
