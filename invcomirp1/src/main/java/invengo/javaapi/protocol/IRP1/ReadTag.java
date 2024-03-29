package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.core.Util;
import invengo.javaapi.core.Util.LogType;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 标签扫描
 *
 * @author dp732
 *
 */
public class ReadTag extends BaseMessage implements IEventHandle {

	public int executeFlag;

	ReadMemoryBank rmb;
	int readTime;
	int stopTime;
	Reader reader;
	private boolean isGetOneTag = false;

	private Object endOfReading = null;

	public Object getEndOfReading() {
		return endOfReading;
	}

	public void setEndOfReading(Object endOfReading) {
		this.endOfReading = endOfReading;
	}

	public ReadTag(ReadMemoryBank rmb) {
		super.isReturn = false;
		this.rmb = rmb;
		super.msgType = MessageType.msgReadTag.get(rmb.toString());
		super.msgBody = new byte[] { 0x01, 0x00, 0x01 };
		if (rmb == ReadMemoryBank.EPC_TID_UserData_6C_2) {
			super.msgBody = new byte[] { 0x01, 0x00, 0x01, 0x04, 0x00, 0x0e };
		}
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody = new byte[] { 0x01, 0x01, 0x01, 0x00, 0x04, 0x00,
					0x00, 0x0e, 0x00, 0x00, 0x00, 0x00, 0x01, 0x08, 0x08 };
		}
	}

	public ReadTag(ReadMemoryBank rmb, int readTime, int stopTime) {
		this(rmb);
		executeFlag = 1;
		this.readTime = readTime;
		this.stopTime = stopTime;
		super.onExecuted.add(this);

	}

	public ReadTag(ReadMemoryBank rmb, boolean isGetOneTag) {
		this(rmb);
		executeFlag = 2;
		super.isReturn = true;
		this.isGetOneTag = isGetOneTag;
		// super.onExecuting.add(this);
		super.onExecuted.add(this);

	}

	public ReadTag() {
	}

	void ReadTag_OnExecuting(Object sender, EventArgs e) {
		this.reader = (Reader) sender;
		super.selectTag(sender, e);
	}

	void ReadTag_OnExecuted(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;
		final SendHelp sh = new SendHelp(reader, this.getTransmitterData(),
				readTime, stopTime);
		Thread t = new Thread() {
			public void run() {
				sh.send();
			}
		};
		t.start();
	}

	class SendHelp {
		Reader reader;
		byte[] tData;
		int readTime;
		int stopTime;

		public SendHelp(Reader reader, byte[] tData, int readTime, int stopTime) {
			this.reader = reader;
			this.tData = tData;
			this.readTime = readTime;
			this.stopTime = stopTime;
		}

		public void send() {
			IMessage p = null;
			if (reader.readerType.equals("500")) {
				p = new PowerOff_500();
			} else {
				p = new PowerOff_800();
			}
			p.setIsReturn(false);
			reader.isStopReadTag = false;
			while (!reader.isStopReadTag) {
				try {
					if (reader.isStopReadTag)
						break;
					Thread.sleep(readTime);
					if (reader.isStopReadTag)
						break;
					reader.send(p);
					if (reader.isStopReadTag)
						break;
					Thread.sleep(stopTime);
					if (reader.isStopReadTag)
						break;
					reader.send(tData);
				} catch (Exception ex) {
					Util.logAndTriggerApiErr(reader.getReaderName(), "FF25", ex
							.getMessage(), LogType.Debug);
					break;
				}
			}
			reader.isStopReadTag = false;
		}
	}

	void ReadTag_OnExecuted2(Object sender, EventArgs e) {
		IMessage msg = null;
		if (this.reader.readerType == "500") {
			msg = new PowerOff_500();
		} else {
			msg = new PowerOff_800();
		}
		if (this.reader.send(msg)) {
			if (endOfReading != null) {
				endOfReading.notifyAll();
				endOfReading = null;
			}
		}
	}

	public enum ReadMemoryBank {
		EPC_6C, TID_6C, EPC_TID_UserData_6C, EPC_TID_UserData_6C_2, ID_6B, ID_UserData_6B, EPC_6C_ID_6B, TID_6C_ID_6B, EPC_TID_UserData_6C_ID_UserData_6B, EPC_TID_UserData_Received_6C_ID_UserData_6B, EPC_PC_6C
	}

	byte antenna = 0x01;

	public byte getAntenna() {
		return antenna;
	}

	public void setAntenna(byte antenna) {
		this.antenna = antenna;
		super.msgBody[0] = antenna;
	}

	byte q = 0x00;

	public byte getQ() {
		return q;
	}

	public void setQ(byte q) {
		this.q = q;
		if (rmb == ReadMemoryBank.EPC_6C || rmb == ReadMemoryBank.TID_6C
				|| rmb == ReadMemoryBank.EPC_TID_UserData_6C
				|| rmb == ReadMemoryBank.EPC_TID_UserData_6C_2
				|| rmb == ReadMemoryBank.EPC_6C_ID_6B
				|| rmb == ReadMemoryBank.EPC_PC_6C
				|| rmb == ReadMemoryBank.TID_6C_ID_6B) {
			super.msgBody[1] = q;
		} else if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[3] = q;
		}
	}

	boolean isLoop = true;

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
		if (rmb != ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[2] = (byte) ((isLoop) ? 0x01 : 0x00);
		} else {
			super.msgBody[1] = (byte) ((isLoop) ? 0x01 : 0x00);
		}
	}

	byte tidLen = 0x04;

	public byte getTidLen() {
		return tidLen;
	}

	public void setTidLen(byte tidLen) {
		this.tidLen = tidLen;
		if (rmb == ReadMemoryBank.EPC_TID_UserData_6C_2) {
			super.msgBody[3] = tidLen;
		} else if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[4] = tidLen;
		}
	}

	byte userDataPtr_6C;

	public byte getUserDataPtr_6C() {
		return userDataPtr_6C;
	}

	public void setUserDataPtr_6C(byte userDataPtr) {
		this.userDataPtr_6C = userDataPtr;
		byte[] ptrArray = EVB.convertToEvb(userDataPtr_6C);
		if (rmb == ReadMemoryBank.EPC_TID_UserData_6C_2) {
			byte[] body = new byte[5 + ptrArray.length];
			System.arraycopy(super.msgBody, 0, body, 0, 4);
			System.arraycopy(ptrArray, 0, body, 4, ptrArray.length);
			System.arraycopy(super.msgBody, super.msgBody.length - 1, body,
					body.length - 1, 1);
			super.msgBody = body;
		} else if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			byte[] body = new byte[14 + ptrArray.length];
			System.arraycopy(super.msgBody, 0, body, 0, 6);
			System.arraycopy(ptrArray, 0, body, 6, ptrArray.length);
			System.arraycopy(super.msgBody, super.msgBody.length - 8, body,
					body.length - 8, 8);
			super.msgBody = body;
		}
	}

	byte userDataLen_6C = 0xe;

	public byte getUserDataLen_6C() {
		return userDataLen_6C;
	}

	public void setUserDataLen_6C(byte userDataLen) {
		this.userDataLen_6C = userDataLen;
		if (rmb == ReadMemoryBank.EPC_TID_UserData_6C_2) {
			super.msgBody[super.msgBody.length - 1] = userDataLen_6C;
		} else if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[super.msgBody.length - 8] = userDataLen_6C;
		}
	}

	byte userDataPtr_6B;

	public byte getUserDataPtr_6B() {
		return userDataPtr_6B;
	}

	public void setUserDataPtr_6B(byte userDataPtr) {
		userDataPtr_6B = (byte) (userDataPtr + 8);
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[super.msgBody.length - 2] = userDataPtr_6B;
		}
	}

	byte userDataLen_6B = 0x08;

	public byte getUserDataLen_6B() {
		return userDataLen_6B;
	}

	public void setUserDataLen_6B(byte userDataLen) {
		userDataLen_6B = userDataLen;
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[super.msgBody.length - 1] = userDataLen_6B;
		}
	}

	byte reservedLen = 0x00;

	public byte getReservedLen() {
		return reservedLen;

	}

	public void setReservedLen(byte reservedLen) {
		this.reservedLen = reservedLen;
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[super.msgBody.length - 1] = reservedLen;
		}
	}

	byte[] pwd = new byte[4];

	public byte[] getAccessPwd() {
		return pwd;

	}

	public void setAccessPwd(byte[] pwd) {
		this.pwd = pwd;
		if (pwd.length > 4 || pwd == null) {
			throw new RuntimeException("Password error!");
		}
		if (pwd.length < 4) {
			byte[] p = new byte[pwd.length];
			System.arraycopy(pwd, 0, p, 0, pwd.length);
			pwd = new byte[4];
			System.arraycopy(p, 0, pwd, 4 - p.length, p.length);
		}
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			System
					.arraycopy(pwd, 0, super.msgBody, super.msgBody.length - 7,
							4);
		}
	}

	byte readTimes_6C = 0x01;

	public byte getReadTimes_6C() {
		return readTimes_6C;
	}

	public void setReadTimes_6C(byte readTimes) {
		this.readTimes_6C = readTimes;
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[2] = readTimes_6C;
		}

	}

	byte readTimes_6B = 0x01;

	public byte getReadTimes_6B() {
		return readTimes_6B;
	}

	public void setReadTimes_6B(byte readTimes) {
		readTimes_6B = readTimes;
		if (rmb == ReadMemoryBank.EPC_TID_UserData_Received_6C_ID_UserData_6B) {
			super.msgBody[super.msgBody.length - 3] = readTimes_6B;
		}
	}

	public ReceivedInfo getReceivedMessage() {
		if (rxData == null) {
			return null;
		}
		return new ReceivedInfo(null, this.reader, super.rxData);

	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {
		private List<RXD_TagData> list_RXD_TagData = new ArrayList<RXD_TagData>();

		public RXD_TagData[] getList_RXD_TagData() {
			return list_RXD_TagData.toArray(new RXD_TagData[] {});
		}

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public ReceivedInfo(byte[] buff, Reader reader, byte[] myBuff) {
			this(buff);
			Decode dc = new Decode();
			int p = 0;
			while (p < myBuff.length - 1) {
				int len = myBuff[p] * 256 + myBuff[p + 1] + 4;
				byte[] data = new byte[len];
				if (p + len > myBuff.length) {
					break;
				}
				System.arraycopy(myBuff, p, data, 0, len);
				p += len;
				IMessageNotification msg = dc.parseMessageNoticefaction(data);
				if (msg == null || msg.getStatusCode() != 0) {
					continue;
				}
				RXD_TagData rxdMsg = new RXD_TagData(reader, msg);
				if (!rxdMsg.getReceivedMessage().getTagType().equals("")) {
					list_RXD_TagData.add(rxdMsg);
				}
			}
		}
	}

	public boolean isGetOneTag() {
		return isGetOneTag;
	}

	public void setGetOneTag(boolean isGetOneTag) {
		this.isGetOneTag = isGetOneTag;
	}

	/**
	 * 重置发送前的状态
	 */
	public void reset() {
		super.statusCode = -1;
		super.setReceivedData(null);
	}

	/**
	 * 启用选择指令
	 *
	 * @param tagID
	 *            匹配数据
	 * @param tagIDType
	 *            匹配区域
	 */
	public void enableSelectTag(byte[] tagID, MemoryBank tagIDType) {
		super.tagID = tagID;
		super.tagIDType = tagIDType;
	}

	/**
	 * 禁用选择指令
	 */
	public void disableSelectTag() {
		super.tagID = null;
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		if (executeFlag == 1) {
			ReadTag_OnExecuted(sender, e);
		} else if (executeFlag == 2) {
			ReadTag_OnExecuting(sender, e);
			ReadTag_OnExecuted2(sender, e);
		}
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		if (executeFlag == 1) {
			ReadTag_OnExecuted(sender, e);
		} else if (executeFlag == 2) {
			ReadTag_OnExecuting(sender, e);
			ReadTag_OnExecuted2(sender, e);
		}
	}
}
