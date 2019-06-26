package invengo.javaapi.core;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.net.ssl.SSLSocket;

import invengo.javaapi.handle.IBuffReceivedHandle;
import invengo.javaapi.handle.IMsgReceivedHandle;

public abstract class ICommunication {

	protected String readerName;
	protected IProcess iProcess;
	protected boolean isConnected;
	protected Socket socket;
	protected SSLSocket sslSocket;
	public List<IMsgReceivedHandle> OnMsgReceived = new ArrayList<IMsgReceivedHandle>();
	public List<IBuffReceivedHandle> onBuffReceived = new ArrayList<IBuffReceivedHandle>();

	public abstract boolean open(String connString) throws Exception;

	public abstract int send(byte[] data);

	public abstract void close();

	private MessageInfo info;

	private Object lockEvent = new Object();
	private Object lockInfo = new Object();
	private volatile boolean isReturnReadTag = false;
	private volatile boolean isGetOneTag = false;

	private BaseReader reader;

	public void setReader(BaseReader reader) {
		this.reader = reader;
	}

	public BaseReader getReader() {
		return this.reader;
	}

	boolean send(IMessage msg, int timeout) {

		String msgType = msg.getMessageType();
		msgType = msgType.substring(msgType.lastIndexOf('.') + 1);
		// if ("ReadTag".equals(msgType) && msg.getIsReturn()) {
		// isReturnReadTag = true;
		// if (msg.getStatusCode() == -2)
		// isGetOneTag = true;
		// }

		boolean isSuc = false;
		if (msg.getIsReturn()) {
			synchronized (lockEvent) {
				synchronized (lockInfo) {
					info = new MessageInfo();
					info.setMsg(msg);
					msg.setPortType(iProcess.getPortType());
				}
				int rc = send(msg.getTransmitterData());
				if (rc > 0 && rc == msg.getTransmitterData().length) {
					synchronized (info.getEv()) {
						isSuc = wait(timeout, info.getEv());
						if ("DspUpdate".equals(msgType)) {
							isSuc = true;
						}
						if (isSuc) {
							if (msg.getStatusCode() != 0x00) {
								isSuc = false;
							}
						} else if (!isReturnReadTag) {
							msg.setStatusCode(0xFF);// 超时
						}
						synchronized (lockInfo) {
							info = null;
						}
					}
				}
			}
		} else {
			msg.setPortType(iProcess.getPortType());
			int rc = send(msg.getTransmitterData());
			if (rc > 0 && rc == msg.getTransmitterData().length) {
				isSuc = true;
			}
		}
		if (isReturnReadTag) {
			isReturnReadTag = false;
		}
		if (isGetOneTag) {
			isGetOneTag = false;
		}
		// if ("ReadTag".equals(msgType) && msg.getIsReturn()) {
		// isSuc = true;
		// }
		return isSuc;
	}

	private Object lockQBufferObj = new Object();
	private LinkedList<byte[]> qBuffer = new LinkedList<byte[]>();

	protected byte[] bufferQueue;

	public byte[] getBufferQueue() {
		synchronized (lockQBufferObj) {
			byte[] bytes = null;
			if (qBuffer.size() == 0) {
				try {
					lockQBufferObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				bytes = qBuffer.remove();
			} catch (NoSuchElementException e) {
				return new byte[0];
			}
			lockQBufferObj.notify();
			return bytes;
		}
	}

	public void setBufferQueue(byte[] bufferQueue) {
		synchronized (lockQBufferObj) {
			if (qBuffer.size() >= 1024) {
				try {
					lockQBufferObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			qBuffer.add(bufferQueue);
			lockQBufferObj.notify();
		}

	}

	protected Thread threadProcess;

	void process() {
		while (isConnected) {
			List<byte[]> recvMsgs = new ArrayList<byte[]>();
			byte[] bufferQueueData = getBufferQueue();
			iProcess.parse(bufferQueueData, recvMsgs);
			if (recvMsgs != null && recvMsgs.size() > 0) {
				for (byte[] bs : recvMsgs) {
					if (info != null)
						synchronized (info) {
							// 现在的代码
							if (info != null && !info.isDone()) {
								if (isReturnReadTag) {
									if (isGetOneTag) {
										info.msg.setReceivedData(bs);
										if (info.msg.getStatusCode() == 0x00) {
											info.setDone(true);
											info.getEv().notifyAll();
											isGetOneTag = false;
											continue;
										}
									} else {
										int len = bs.length;
										if (info.msg.getReceivedData() != null) {
											if (info.msg.getReceivedData().length > 0) {
												len += info.msg
														.getReceivedData().length;
												byte[] data = new byte[len];
												System.arraycopy(
														info.msg.getReceivedData(),
														0,
														data,
														0,
														info.msg.getReceivedData().length);
												System.arraycopy(
														bs,
														0,
														data,
														info.msg.getReceivedData().length,
														bs.length);
												info.msg.setReceivedData(data);
											}
										} else
											info.msg.setReceivedData(bs);
										continue;
									}
								}
								if (iProcess.getMessageID(bs) == info.msg
										.getMessageID()) {
									info.msg.setReceivedData(bs);
									if (info.msg.getStatusCode() != 0xFF) {
										info.setDone(true);
										info.getEv().notifyAll();
									}
									continue;
								}
							}
						}
					IMessageNotification msg = iProcess
							.parseMessageNoticefaction(bs);
					if (OnMsgReceived != null && msg != null) {
						for (int i = 0; i < OnMsgReceived.size(); i++) {
							OnMsgReceived.get(i).bufferReceivedHandle(msg);
						}
					}
				}
			}
			if (bufferQueueData != null) {
				for (int i = 0; i < onBuffReceived.size(); i++) {
					onBuffReceived.get(i).bufferReceived(bufferQueueData);
				}
			}
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public String getReaderName() {
		return readerName;
	}

	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
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
}
