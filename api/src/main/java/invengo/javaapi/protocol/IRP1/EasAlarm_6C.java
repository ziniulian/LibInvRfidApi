package invengo.javaapi.protocol.IRP1;

/**
 * EAS监控功能设置指令
 *
 * @author dp732
 *
 */
public class EasAlarm_6C extends BaseMessage {

	/**
	 * @param antenna
	 *            天线端口
	 * @param easCfg
	 *            EAS监控配置
	 */
	public EasAlarm_6C(byte antenna, byte easCfg) {
		super.msgBody = new byte[] { antenna, easCfg, 0x00 };// 最后一项为保留域
	}

	public EasAlarm_6C() {

	}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		private byte antenna;// 天线端口
		private byte answerType;// 应答类型

		public ReceivedInfo(byte[] buff) {
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
}
