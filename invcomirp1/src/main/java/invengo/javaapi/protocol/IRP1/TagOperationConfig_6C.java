package invengo.javaapi.protocol.IRP1;

/**
 * 配置标签操作配置指令
 *
 * @author dp732
 *
 */
public class TagOperationConfig_6C extends BaseMessage {

	/**
	 * @param parameter
	 *            配置标签操作配置指令参数类型
	 * @param data
	 *            配置数据
	 */
	public TagOperationConfig_6C(byte parameter, byte[] data) {
		super.msgBody = new byte[data.length + 2];// 指令内容
		super.msgBody[0] = parameter;// 参数类型
		super.msgBody[1] = (byte) data.length;// 参数长度
		System.arraycopy(data, 0, msgBody, 2, data.length);
	}

	public TagOperationConfig_6C() {
	}
}
