package invengo.javaapi.core;

import android.app.Activity;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.net.ssl.SSLSocket;

import invengo.javaapi.handle.IBuffReceivedHandle;
import invengo.javaapi.handle.IMsgReceivedHandle;
import invengo.javaapi.protocol.IRP1.GBAccessReadTag;
import invengo.javaapi.protocol.IRP1.ReadTag;
import invengo.javaapi.protocol.IRP1.Reader;

public abstract class ICommunication {

	protected String readerName;
	protected IProcess iProcess;
	protected boolean isConnected;
	protected Activity context;//BLE
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

	private long startTime;
	boolean send(IMessage msg, int timeout) {
		startTime = System.currentTimeMillis();
		String msgType = msg.getMessageType();
		msgType = msgType.substring(msgType.lastIndexOf('.') + 1);
		if(msg instanceof ReadTag){//用于测温
			ReadTag readTag = (ReadTag) msg;
			if(readTag.isMeasureTemperature()){
				reader.setInitValue(readTag.getInitValue());
				reader.setStepValue(readTag.getStepValue());
			}
		}
		boolean isSuc = false;
		if (msg.getIsReturn()) {
			synchronized (lockEvent) {
				synchronized (lockInfo) {
					info = new MessageInfo();
					info.setMsg(msg);
					msg.setPortType(iProcess.getPortType());
				}
				synchronized (info.getEv()) {
					byte[] sendData = msg.getTransmitterData();
					// InvengoLog.i("REQUEST", String.format("INFO.Message Send - %s", Util.convertByteArrayToHexString(sendData)));
					int rc = send(sendData);
					if (rc > 0 && rc == sendData.length) {
						isSuc = wait(timeout, info.getEv());
						if ("DspUpdate".equals(msgType)) {
							isSuc = true;
						}
						if (isSuc) {
//							System.out.println("Result:" + isSuc + "-MessageType:" + msg.getMessageType());
							if (msg.getStatusCode() != 0x00) {
								isSuc = false;
							}
							// InvengoLog.i("REQUEST", String.format("INFO.Result - %s - MessageType: %s", isSuc, msg.getMessageType()));
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
			// InvengoLog.i("REQUEST", String.format("INFO.Message Send - %s", Util.convertByteArrayToHexString(msg.getTransmitterData())));
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

	protected void process() {
		while (isConnected) {
			List<byte[]> recvMsgs = new ArrayList<byte[]>();
			byte[] bufferQueueData = getBufferQueue();
//			InvengoLog.i("Response", Util.convertByteArrayToHexString(bufferQueueData));
			iProcess.parse(bufferQueueData, recvMsgs);
			if (recvMsgs != null && recvMsgs.size() > 0) {
				for (byte[] bs : recvMsgs) {
					long endTime = System.currentTimeMillis();
					// InvengoLog.i("Response", "Time:" + (endTime - startTime) + "-Response:" + Util.convertByteArrayToHexString(bs));

					if (info != null){
						synchronized (info) {
							if (info != null && info.msg != null && !info.isDone()) {
								
//								if(null == iProcess){//未知原因导致iProcess为NULL的处理.不返回任何消息,做超时处理.
//									InvengoLog.i("Response", String.format("Message type {%s}", info.getMsg().toString()));
//									return;
//								}
								
								if (iProcess.getMessageID(bs) == info.msg.getMessageID()) {
									info.msg.setReceivedData(bs);
									if(info.msg instanceof GBAccessReadTag){
										((GBAccessReadTag)info.msg).setCurrentReader((Reader) this.reader);
									}
									if (info.msg.getStatusCode() != 0xFF) {
										info.setDone(true);
										info.getEv().notifyAll();
//										info.getEv().notify();
//										System.out.println("-Response:" + Util.convertByteArrayToHexString(bs));
									}
									continue;
								}
							}
						}
					}
					IMessageNotification msg = iProcess.parseMessageNoticefaction(bs);
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
	
	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public boolean wait(long timeout, Object object) {
		boolean isSuc = false;
		synchronized (object) {
			try {
//				System.out.println("INFO.start wait.");
				long start = System.currentTimeMillis();
				object.wait(timeout);
				long end = System.currentTimeMillis();
//				System.out.println("INFO.end wait.");
				long time = end - start;
//				InvengoLog.i("Response", "WaitTime:" + time);
				if (time >= timeout) {
					isSuc = false;
				} else {
					isSuc = true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				isSuc = false;
			}
		}
		return isSuc;
	}
}
