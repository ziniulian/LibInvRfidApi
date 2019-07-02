package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/*
 * For Malaysia 807
 * Send command to get tag password.
 * 指令长度	命令字	天线端口	    时隙Q值	操作类型	 CRC
 * (2字节)	 C3H	01H~04H	 00H-0FH	00H~01H	(2字节)
 */
public class ReadTagPassword extends BaseMessage implements IEventHandle{

	private byte antenna = 0x01;
	private byte q = 0x00;
	private byte operateType = 0x00;

	public ReadTagPassword(byte antenna, byte q, byte type){
		super.isReturn = false;
		this.antenna = antenna;
		this.q = q;
		this.operateType = type;

		super.msgBody = new byte[3];
		super.msgBody[0] = this.antenna;
		super.msgBody[1] = this.q;
		super.msgBody[2] = this.operateType;
	}

	public ReadTagPassword(byte antenna, byte q, byte type, byte[] tagID,
						   MemoryBank tagIDType){
		this(antenna, q, type);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}

	//For Reflect
	public ReadTagPassword(){}

	public byte getAntenna() {
		return antenna;
	}

	public void setAntenna(byte antenna) {
		this.antenna = antenna;
		super.msgBody[0] = antenna;
	}

	public byte getQ() {
		return q;
	}

	public void setQ(byte q) {
		this.q = q;
		super.msgBody[1] = q;
	}

	public byte getOperateType() {
		return operateType;
	}

	public void setOperateType(byte operateType) {
		this.operateType = operateType;
		super.msgBody[2] = operateType;
	}

	public ReceivedInfo getReceivedMessage() {
		//data[N]...data[0]-antenna,data[1-4]-password,data[4-N]-tag tid
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

		public byte getAntenna(){
			byte a = 0x01;
			if (buff != null && buff.length >= 1) {
				a = buff[0];
			}
			return a;
		}

		public byte[] getPassword(){
			byte[] password = new byte[4];
			if(null != buff && buff.length >= 5){
				System.arraycopy(buff, 1, password, 0, password.length);
			}
			return password;
		}

		public byte[] getTagTid(){
			byte[] tid = new byte[buff.length - 5];
			if(null != buff && buff.length >= 5){
				System.arraycopy(buff, 5, tid, 0, tid.length);
			}
			return tid;
		}

	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}
