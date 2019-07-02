package invengo.javaapi.protocol.receivedInfo;

import invengo.javaapi.core.ReceivedInfo;

public class EasAlarm6CReceivedInfo extends ReceivedInfo {

	private static final long serialVersionUID = 5538726154568797370L;
	private byte antenna;// 天线端口
	private byte answerType;// 应答类型

	public EasAlarm6CReceivedInfo(byte[] buff) {
		super(buff);
	}

	/**
	 * 获取天线端口
	 *
	 * @return 天线端口
	 */
	public byte getAntenna() {
		if (buff != null && buff.length >= 1) {
			this.antenna = this.buff[0];
		}
		return this.antenna;
	}

	/**
	 * 获取应答类型
	 *
	 * @return 应答类型 00H：EAS监控启动成功 A0H：发现EAS位设置标签
	 */
	public byte getAnswerType() {
		if (buff != null && buff.length >= 2) {
			this.answerType = this.buff[1];// 应答类型
		}
		return this.answerType;
	}

}
