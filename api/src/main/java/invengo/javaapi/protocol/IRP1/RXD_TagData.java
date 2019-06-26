package invengo.javaapi.protocol.IRP1;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.Log;
import invengo.javaapi.protocol.IRP1.BaseMessageNotification;
import invengo.javaapi.protocol.IRP1.Reader;

public class RXD_TagData extends BaseMessageNotification {

	Reader reader;
	IMessageNotification msg;

	public RXD_TagData(Reader reader, IMessageNotification msg) {
		this.reader = reader;
		this.msg = msg;
		this.msgID = msg.getMessageID();
		this.setReceivedData(msg.getReceivedData());
	}

	public ReceivedInfo getReceivedMessage() {
		return new ReceivedInfo(null, reader, msg);
	}

	public class ReceivedInfo extends invengo.javaapi.core.ReceivedInfo {

		private String readerName = "";

		public String getReaderName() {
			return readerName;
		}

		private String tagType = "";

		public String getTagType() {
			return tagType;
		}

		private byte[] epc = null;

		public byte[] getEPC() {
			return epc;
		}

		private byte[] epc_pc = null;

		public byte[] getEPC_PC() {
			return epc_pc;
		}

		private byte[] tid = null;

		public byte[] getTID() {
			return tid;
		}

		private byte[] userdata = null;

		public byte[] getUserData() {
			return userdata;
		}

		private byte[] reserved = null;

		public byte[] getReserved() {
			return reserved;
		}

		private byte antenna;

		public byte getAntenna() {
			return antenna;
		}

		private byte[] rssi = null;

		public byte[] getRSSI() {
			return rssi;
		}

		private byte[] rxdtime = null;

		public byte[] getRXDTime() {
			return rxdtime;
		}

		public ReceivedInfo(byte[] buff) {
			super(buff);
		}

		public ReceivedInfo(byte[] buff, Reader reader, IMessageNotification msg) {
			this(buff);
			if (msg.getStatusCode() != 0) {
				return;
			}
			this.readerName = reader.getReaderName();
			String msgType = msg.getMessageType();
			msgType = msgType.substring(msgType.lastIndexOf('.') + 1);
			if (msgType.equals("RXD_ID_6B")) {
				RXD_ID_6B m = (RXD_ID_6B) msg;
				this.tagType = "6B";
				this.tid = m.getReceivedMessage().getTID();
				this.antenna = m.getReceivedMessage().getAntenna();
				this.rssi = m.getReceivedMessage().getRssi();
			} else if (msgType.equals("RXD_ID_UserData_6B")) {
				RXD_ID_UserData_6B m = (RXD_ID_UserData_6B) msg;
				this.tagType = "6B";
				this.tid = m.getReceivedMessage().getTID();
				this.userdata = m.getReceivedMessage().getUserData();
				this.antenna = m.getReceivedMessage().getAntenna();
			} else if (msgType.equals("RXD_ID_UserData_6B_2")) {
				RXD_ID_UserData_6B_2 m = (RXD_ID_UserData_6B_2) msg;
				this.tagType = "6B";
				this.tid = m.getReceivedMessage().getID();
				this.userdata = m.getReceivedMessage().getUserData();
				this.antenna = m.getReceivedMessage().getAntenna();
				this.rssi = m.getReceivedMessage().getRSSI();
				this.rxdtime = m.getReceivedMessage().getReadTime();
			} else if (msgType.equals("RXD_EPC_6C")) {
				RXD_EPC_6C m = (RXD_EPC_6C) msg;
				this.tagType = "6C";
				this.antenna = m.getReceivedMessage().getAntenna();
				byte[] myEpc = m.getReceivedMessage().getEPC();
				if (reader.readerType.equals("500")) {
					this.epc = new byte[myEpc.length - 2];
					System.arraycopy(myEpc, 0, this.epc, 0, myEpc.length - 2);
					this.rssi = new byte[2];
					System.arraycopy(myEpc, myEpc.length - 2, this.rssi, 0, 2);
				} else {
					if (reader.isUtcEnable && myEpc.length > 8) {
						this.rxdtime = new byte[8];
						byte[] t_epc = new byte[myEpc.length - 8];
						System.arraycopy(myEpc, 0, t_epc, 0, t_epc.length);
						System.arraycopy(myEpc, t_epc.length, this.rxdtime, 0,
								8);
						myEpc = t_epc;
					}
					if (reader.isRssiEnable && myEpc.length > 2) {
						byte[] t_epc = new byte[myEpc.length - 2];
						System.arraycopy(myEpc, 0, t_epc, 0, t_epc.length);
						this.rssi = new byte[2];
						System.arraycopy(myEpc, t_epc.length, this.rssi, 0, 2);
						myEpc = t_epc;
					}
					this.epc = myEpc;
				}
			} else if (msgType.equals("RXD_TID_6C")) {
				RXD_TID_6C m = (RXD_TID_6C) msg;
				this.tagType = "6C";
				this.antenna = m.getReceivedMessage().getAntenna();
				byte[] mytid = m.getReceivedMessage().getTID();
				if (reader.readerType.equals("500") && mytid.length > 2) {
					this.tid = new byte[mytid.length - 2];
					System.arraycopy(mytid, 0, this.tid, 0, this.tid.length);
					this.rssi = new byte[2];
					System.arraycopy(mytid, this.tid.length, this.rssi, 0, 2);
				} else {
					this.tid = m.getReceivedMessage().getTID();
				}
			} else if (msgType.equals("RXD_TID_6C_2")) {
				RXD_TID_6C_2 m = (RXD_TID_6C_2) msg;
				this.tagType = "6C";
				this.tid = m.getReceivedMessage().getTID();
				this.antenna = m.getReceivedMessage().getAntenna();
				this.rxdtime = m.getReceivedMessage().getTime();

				RXD_TID_6C_2 rp = new RXD_TID_6C_2(m.getReceivedMessage()
						.getReaderID(), m.getReceivedMessage().getDataNO());
				reader.send(rp);
			} else if (msgType.equals("RXD_EPC_TID_UserData_6C")) {
				RXD_EPC_TID_UserData_6C m = (RXD_EPC_TID_UserData_6C) msg;
				this.tagType = "6C";
				this.tid = m.getReceivedMessage().getTID();
				this.userdata = m.getReceivedMessage().getUserData();
				this.antenna = m.getReceivedMessage().getAntenna();
				byte[] myepc = m.getReceivedMessage().getEPC();
				if (reader.readerType.equals("800")) {
					if (reader.isUtcEnable && myepc.length > 8) {
						byte[] t_epc = new byte[myepc.length - 8];
						System.arraycopy(myepc, 0, t_epc, 0, t_epc.length);
						this.rxdtime = new byte[8];
						System.arraycopy(myepc, t_epc.length, this.rxdtime, 0,
								8);
						myepc = t_epc;
					}
					if (reader.isRssiEnable && myepc.length > 2) {
						byte[] t_epc = new byte[myepc.length - 2];
						System.arraycopy(myepc, 0, t_epc, 0, t_epc.length);
						this.rssi = new byte[2];
						System.arraycopy(myepc, t_epc.length, this.rssi, 0, 2);
						myepc = t_epc;
					}
					this.epc = myepc;
				} else {
					this.epc = m.getReceivedMessage().getEPC();
				}
			} else if (msgType.equals("RXD_EPC_TID_UserData_6C_2")) {
				RXD_EPC_TID_UserData_6C_2 m = (RXD_EPC_TID_UserData_6C_2) msg;
				this.tagType = "6C";
				this.tid = m.getReceivedMessage().getTID();
				this.userdata = m.getReceivedMessage().getUserData();
				this.antenna = m.getReceivedMessage().getAntenna();

				byte[] myepc = m.getReceivedMessage().getEPC();
				if (reader.readerType.equals("800")) {
					if (reader.isUtcEnable && myepc.length > 8) {
						byte[] t_epc = new byte[myepc.length - 8];
						System.arraycopy(myepc, 0, t_epc, 0, t_epc.length);
						this.rxdtime = new byte[8];
						System.arraycopy(myepc, t_epc.length, this.rxdtime, 0,
								8);
						myepc = t_epc;
					}
					if (reader.isRssiEnable && myepc.length > 2) {
						byte[] t_epc = new byte[myepc.length - 2];
						System.arraycopy(myepc, 0, t_epc, 0, t_epc.length);
						this.rssi = new byte[2];
						System.arraycopy(myepc, t_epc.length, this.rssi, 0, 2);
						myepc = t_epc;
					}
					this.epc = myepc;
				} else {
					this.epc = m.getReceivedMessage().getEPC();
				}
			} else if (msgType.equals("RXD_EPC_PC_6C")) {
				RXD_EPC_PC_6C m = (RXD_EPC_PC_6C) msg;
				this.tagType = "6C";
				this.epc = m.getReceivedMessage().getEPC();
				this.epc_pc = m.getReceivedMessage().getPc();
				this.antenna = m.getReceivedMessage().getAntenna();
				this.rssi = m.getReceivedMessage().getRssi();
			} else if (msgType.equals("RXD_EPC_TID_UserData_Reserved_6C")) {
				RXD_EPC_TID_UserData_Reserved_6C m = (RXD_EPC_TID_UserData_Reserved_6C) msg;
				this.tagType = "6C";
				this.epc = m.getReceivedMessage().getEPC();
				this.tid = m.getReceivedMessage().getTID();
				this.userdata = m.getReceivedMessage().getUserData();
				this.reserved = m.getReceivedMessage().getReserved();
				this.antenna = m.getReceivedMessage().getAntenna();
				this.rssi = m.getReceivedMessage().getRSSI();
				this.rxdtime = m.getReceivedMessage().getReadTime();
			} else if (msgType.equals("PcSendTime_500")) {
				PcSendTime_500 m = (PcSendTime_500) msg;
				if (m.getStatusCode() == 0) {
					int d = (int) (System.currentTimeMillis() / 1000);
					byte[] time = new byte[4];
					time[0] = (byte) (d >> 24);
					time[1] = (byte) ((d >> 16) & 0xFF);
					time[2] = (byte) ((d >> 8) & 0xFF);
					time[3] = (byte) (d & 0xFF);
					PcSendTime_500 order = new PcSendTime_500(m
							.getReceivedMessage().getReaderID(), time);
					reader.send(order);
				} else {
					Log.debug(m.getErrInfo());
				}
			} else if (msgType.equals("Keepalive")) {
				Keepalive m = (Keepalive) msg;
				long time = System.currentTimeMillis();
				int s = (int) (time / 1000);
				int ms = (int) (time % 1000);
				byte[] data = new byte[8];
				ByteBuffer byteBuff = ByteBuffer.allocate(8);
				IntBuffer buff2 = byteBuff.asIntBuffer();
				buff2.put(s);
				buff2.put(ms);
				buff2.flip();
				byteBuff.get(data);
				Keepalive keepalive = new Keepalive(m.getReceivedMessage()
						.getSequence(), data);
				reader.send(keepalive);
			} else if (msgType.equals("CustomBusinessData")) {
				CustomBusinessData m = (CustomBusinessData) msg;
				reader.send(new CustomBusinessData(m.getReceivedMessage()
						.getType(), m.getReceivedMessage().getMessageID()));
			}
		}
	}
}