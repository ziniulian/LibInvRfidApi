package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * Impinj QT指令
 *
 * @author dp732
 *
 */
public class QT_6C extends BaseMessage implements IEventHandle {

	/**
	 * @param antenna
	 *            天线端口
	 * @param accessPwd
	 *            密码(4字节)
	 * @param opType
	 *            操作类型（R/W）:0，查询标签的原有QT属性;1，设置标签的QT属性
	 * @param persistent
	 *            配置持久性（persistent）：0，配置仅在标签本次上电过程中有效;1，配置可一直保存
	 * @param payload
	 *            配置参数 第一字节常用位控制模式意义如下表，第二字节保留 比特 7 6 5 4 3 2 1 0 定义 QT_SR
	 *            QT_MEM 保留 保留 保留 保留 间隔 保留 QT_SR：1：标签在开状态和安全态时会减少响应距离（或仅在近距离响应）
	 *            0：标签不减少响应距离 QT_MEM 1：标签存储器映射为公共模式 0: 标签存储器映射为私密模式
	 *
	 */
	public QT_6C(byte antenna, byte[] accessPwd, byte opType, byte persistent,
				 byte[] payload) {
		if (accessPwd==null||payload==null) {
			throw new IllegalArgumentException();
		}
		super.msgBody = new byte[9];
		super.msgBody[0] = antenna;
		System.arraycopy(accessPwd, 0, msgBody, 1, accessPwd.length);
		super.msgBody[accessPwd.length + 1] = opType;
		super.msgBody[accessPwd.length + 2] = persistent;
		System.arraycopy(payload, 0, msgBody, accessPwd.length + 3,
				payload.length);
	}

	public QT_6C(byte antenna, byte[] accessPwd, byte opType, byte persistent,
				 byte[] payload, byte[] tagID, MemoryBank tagIDType) {
		this(antenna, accessPwd, opType, persistent, payload);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	public QT_6C() {

	}

	public ReceivedInfo getReceivedMessage() {
		byte[] data = Decode.getRxMessageData(super.rxData);
		if (data == null) {
			return null;
		}
		return new ReceivedInfo(data);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public byte getAntenna() {
			if (buff != null && buff.length >= 1) {
				return buff[0];
			}
			return 0x00;
		}

		byte[] payload = null;

		public byte[] getPayload() {
			if (buff != null && buff.length >= 3) {
				payload = new byte[buff.length - 1];
				System.arraycopy(buff, 1, payload, 0, payload.length);
			}
			return payload;
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);

	}
}
