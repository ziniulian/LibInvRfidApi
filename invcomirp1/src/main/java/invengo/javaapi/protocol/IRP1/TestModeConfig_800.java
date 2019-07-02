package invengo.javaapi.protocol.IRP1;

/**
 * 读写器测试模式设置指令
 *
 * @author dp732
 *
 */
public class TestModeConfig_800 extends BaseMessage {

	/**
	 * @param parameter 读写器测试模式设置指令的测试模式
	 * @param data 测试参数
	 */
	public TestModeConfig_800(byte parameter, byte[] data) {
		// 指令内容
		super.msgBody = new byte[data.length + 1];
		super.msgBody[0] = parameter; // 参数类型
		System.arraycopy(data, 0, msgBody, 1, data.length);
	}

	public TestModeConfig_800(){}
}
