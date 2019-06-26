package invengo.javaapi.protocol.IRP1;

/**
 * 标签灭活指令
 */

public class KillTag_6C extends BaseMessage{

	/**
	 * @param antenna 天线端口
	 * @param killPwd 灭活密码
	 * @param epcData EPC码
	 */
	public KillTag_6C(byte antenna, byte[] killPwd, byte[] epcData)
	{
		super.msgBody = new byte[5 + epcData.length];
		super.msgBody[0] = antenna;
		System.arraycopy(killPwd, 0, msgBody, 1, 4);
		System.arraycopy(epcData, 0, msgBody, 5, epcData.length);
	}

	public KillTag_6C() { }

}
