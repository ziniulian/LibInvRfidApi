package invengo.javaapi.protocol.IRP1;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.Util;
import invengo.javaapi.core.Util.LogType;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;

public class Reader extends BaseReader implements
		IMessageNotificationReceivedHandle {

	/**
	 *
	 */
	private static final long serialVersionUID = -5493947669141168755L;
	protected boolean isUtcEnable = false;
	protected boolean isRssiEnable = false;
	protected String readerType = "";
	protected volatile boolean isStopReadTag = false;

	public List<IMessageNotificationReceivedHandle> onMessageNotificationReceived = new ArrayList<IMessageNotificationReceivedHandle>();

	public Reader(String readerName, String portType, String connStr) {
		super(readerName, "IRP1", portType, connStr);
		super.onMessageNotificationReceived.add(this);
	}

	public Reader(String readerName) {
		super(readerName);
		super.onMessageNotificationReceived.add(this);
	}

	public Reader(Socket server) {
		super(server, "IRP1");
		super.onMessageNotificationReceived.add(this);
	}

	public void messageNotificationReceivedHandle(BaseReader reader,
												  IMessageNotification msg) {
		if (msg.getStatusCode() != 0) {
			if (onMessageNotificationReceived != null) {
				for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
					onMessageNotificationReceived.get(i)
							.messageNotificationReceivedHandle(reader, msg);
				}
			}
			return;
		}
		RXD_TagData rxdMsg = new RXD_TagData((Reader) reader, msg);
		if (!rxdMsg.getReceivedMessage().getTagType().equals("")) {
			synchronized (lockinfo) {
				if (this.info != null) {
					if (this.info.isGetOneTag && !this.info.isDone) {
						info.msg.setReceivedData(msg.getReceivedData());
						this.info.isDone = true;
						synchronized (info.getEv()) {
							info.getEv().notify();
						}
					} else {
						int len = msg.getReceivedData().length;
						if (info.msg.getReceivedData() != null) {
							if (info.msg.getReceivedData().length > 0) {
								len += info.msg.getReceivedData().length;
								byte[] data = new byte[len];
								System.arraycopy(info.msg.getReceivedData(), 0,
										data, 0,
										info.msg.getReceivedData().length);
								System.arraycopy(msg.getReceivedData(), 0,
										data,
										info.msg.getReceivedData().length, msg
												.getReceivedData().length);
								info.msg.setReceivedData(data);

							}
						} else
							info.msg.setReceivedData(msg.getReceivedData());
					}
					return;
				}
			}
			if (onMessageNotificationReceived != null) {
				for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
					onMessageNotificationReceived.get(i)
							.messageNotificationReceivedHandle((Reader) reader,
									rxdMsg);
				}
			}
		} else {
			if (onMessageNotificationReceived != null) {
				for (int i = 0; i < onMessageNotificationReceived.size(); i++) {
					onMessageNotificationReceived.get(i)
							.messageNotificationReceivedHandle((Reader) reader,
									msg);
				}
			}
		}
	}

	public boolean connect() {
		boolean isConn = super.connect();
		if (isConn) {
			// 确定读写器系列
			if (super.send(new Gpi_800((byte) 0x00), 200)) {
				readerType = "800";
			} else {
				readerType = "500";
			}
			// 查询型号
			if (modelNumber.equals("unknown")) {
				String mn = "";
				SysQuery_800 msg = new SysQuery_800((byte) 0x20);
				if (super.send(msg, 200)) {
					mn = new String(msg.getReceivedMessage().getQueryData());
					super.setModelNumber(mn);
					if (super.getModelNumber() == "XC-RF812"
							|| super.getModelNumber() == "XC-RF853") {
						readerType = "800";
					}
				} else {
					modelNumber = "XCRF-502E";
				}
				if (mn.indexOf("XC") == -1) {
					if (readerType.equals("800")) {
						msg = new SysQuery_800((byte) 0x21);
						if (super.send(msg, 200)) {
							mn = new String(msg.getReceivedMessage()
									.getQueryData());
							{
								mn = mn.substring(0, mn.length() - 1);
								String v = mn;
								String ver = "";
								List<String> list = Arrays.asList(new String[] {
										"0", "1", "2", "3", "4", "5", "6", "7",
										"8", "9" });
								for (int i = 0; i < v.length(); i++) {
									String s = v.substring(i, i + 1);
									if (list.contains(s)) {
										ver = ver + s;
									}
								}
								modelNumber = ver;
							}
							if (modelNumber.indexOf("XC") == -1) {
								if (modelNumber.equals("806")) {
									modelNumber = "XC-RF806";
								} else if (modelNumber.equals("860")) {
									modelNumber = "XC-RF860";
								} else {
									modelNumber = "XC-RF" + modelNumber;
								}
							}
						}
					}
				}
			} else {
				if (super.getModelNumber() == "XC-RF812"
						|| super.getModelNumber() == "XC-RF853") {
					readerType = "800";
				}
			}
			// 查询RSSI功能
			if (readerType.equals("800")
					&& super.getModelNumber() != "XC-RF812"
					&& super.getModelNumber() != "XC-RF853") {
				SysQuery_800 msg = new SysQuery_800((byte) 0x14);
				if (super.send(msg, 200)) {
					if (msg.getReceivedMessage().getQueryData() != null
							&& msg.getReceivedMessage().getQueryData().length > 0) {
						if (msg.getReceivedMessage().getQueryData()[0] == 0x01) {
							isRssiEnable = true;
						} else {
							isRssiEnable = false;
						}
					}
				} else {
					isRssiEnable = false;
				}
			} else if (super.modelNumber.toUpperCase().indexOf("502E") != -1
					|| super.modelNumber.toUpperCase().equals("XC-RF811")) {
				isRssiEnable = true;
			}
			// 查询UTC功能
			if (readerType.equals("800")
					&& super.getModelNumber() != "XC-RF812"
					&& super.getModelNumber() != "XC-RF853") {
				SysQuery_800 msg = new SysQuery_800((byte) 0x18);
				if (super.send(msg, 200)) {
					if (msg.getReceivedMessage().getQueryData() != null
							&& msg.getReceivedMessage().getQueryData().length > 0) {
						if (msg.getReceivedMessage().getQueryData()[0] == 0x01) {
							isUtcEnable = true;
						} else {
							isUtcEnable = false;
						}
					}
				} else {
					isUtcEnable = false;
				}
			}
		}
		return isConn;
	}

	private Object lockEvent = new Object();
	private Object lockinfo = new Object();

	public boolean send(IMessage msg) {
		if (msg instanceof ReadTag) {
			ReadTag rt = ((ReadTag) msg);
			if (msg.getIsReturn()) {
				try {
					msg.triggerOnExecuting(this);
				} catch (RuntimeException e) {
					Util.logAndTriggerApiErr(readerName, "FF26",
							e.getMessage(), LogType.Debug);
					return false;
				}
				boolean isGetOneTag = rt.isGetOneTag();
				synchronized (lockEvent) {
					synchronized (lockinfo) {
						info = new MessageInfo();
						info.setMsg(msg);
						info.setGetOneTag(isGetOneTag);
					}
					msg.setPortType(this.portType);
					byte[] sd = msg.getTransmitterData();
					boolean rc = super.send(sd);
					if (rc) {
						synchronized (info.getEv()) {
							boolean isSuc = wait(msg.getTimeOut(), info.getEv());
							if (!isSuc) {
								if (isGetOneTag) {
									msg.setStatusCode(0xff);// 超时
								} else {
									msg.setStatusCode(0x00);
								}
							}
						}
					}
					// synchronized (lockinfo) {
					// info = null;
					// }
				}
				msg.triggerOnExecuted(this);
				rt.setEndOfReading(new Object());
				wait(500, rt.getEndOfReading());
				synchronized (lockinfo) {
					info = null;
				}
				return (msg.getStatusCode() == 0x00);
			}
		}
		return super.send(msg);
	}

	public boolean wait(long timeout, Object object) {
		boolean isSuc = false;
		try {
			long start = System.currentTimeMillis();
			object.wait(timeout);
			long end = System.currentTimeMillis();
			long time = end - start;
			if (time >= timeout) {
				isSuc = false;
			} else {
				isSuc = true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			isSuc = false;
		}
		return isSuc;
	}

	private MessageInfo info;

	class MessageInfo {
		public MessageInfo ev = this;
		public IMessage msg = null;
		public volatile boolean isGetOneTag = false;
		public volatile boolean isDone = false;

		public MessageInfo getEv() {
			return ev;
		}

		public void setEv(MessageInfo ev) {
			this.ev = ev;
		}

		public IMessage getMsg() {
			return msg;
		}

		public void setMsg(IMessage msg) {
			this.msg = msg;
		}

		public boolean isGetOneTag() {
			return isGetOneTag;
		}

		public void setGetOneTag(boolean isGetOneTag) {
			this.isGetOneTag = isGetOneTag;
		}

		public boolean isDone() {
			return isDone;
		}

		public void setDone(boolean isDone) {
			this.isDone = isDone;
		}

	}
}