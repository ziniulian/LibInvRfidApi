package invengo.javaapi.communication;

import invengo.javaapi.core.ICommunication;
import invengo.javaapi.core.Log;
import invengo.javaapi.core.Util;
import invengo.javaapi.core.Util.LogType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetSocket extends ICommunication {

	private int portNum = 7086;// 读写器端口
	private String hostName = "192.168.0.210";// 读写器IP
	private Thread threadRun = null;// 监听线程
	private InputStream reader;
	private OutputStream writer;
	private Object lockObj = new Object();

	public NetSocket() {

	}

	private void open() {

		try {
			if (socket == null) {
				socket = new Socket();
			}
			if (!socket.isConnected()) {
				InetSocketAddress address = new InetSocketAddress(hostName, portNum);
				socket.connect(address, 2000);
			}
			if (!socket.isConnected()) {
				socket = null;
				return;
			}
		} catch (Exception e) {
			super.setConnected(false);
			try {
				socket.close();
			} catch (IOException e1) {
			}
			Util.logAndTriggerApiErr(super.readerName, "FF19", e.getMessage(), LogType.Fatal);
			throw new RuntimeException(e);
		}
		if (threadRun == null || !threadRun.isAlive()) {
			threadRun = new Thread() {
				public void run() {
					runClient();
				}
			};
			threadRun.start();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

	}

	public boolean open(String connString) {
		if (socket == null) {
			if (connString.indexOf(':') != -1) {
				this.hostName = connString.substring(0, connString.indexOf(':'));
				this.portNum = Integer.parseInt(connString.substring(connString.indexOf(':') + 1));
			} else {
				this.hostName = connString;
			}
		}
		open();
		return super.isConnected();
	}

	public void close() {
		super.setConnected(false);
		this.isConnected = false;// 再次设置
		//		if (writer != null) {
		//			try {
		//				writer.close();
		//				writer = null;
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		if (reader != null) {
		//			try {
		//				reader.close();
		//				reader = null;
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		if (socket != null) {
		//			try {
		//				if(!socket.isClosed()){
		//					socket.close();
		//				}
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
		if (threadRun != null) {
			if (threadRun.isAlive()) {
				threadRun = null;
			}
		}
	}

	public int send(byte[] data) {
		if (!super.isConnected || (socket == null || (socket != null && !socket.isConnected()))) {
			super.isConnected = false;
			Util.logAndTriggerApiErr(super.readerName, "FF22", "",LogType.Warn);
			return 0;
		}
		int sl = 0;
		if (data != null) {
			try {
				if (super.isConnected()) {
					tcpSend(data);// 发送
					sl = data.length;
				}
			} catch (Exception e) {
				Util.logAndTriggerApiErr(super.readerName, "FF21", e.getMessage(), LogType.Error);
				sl = 0;
			}
		} else {
			Util.logAndTriggerApiErr(super.readerName, "FF23", "",LogType.Info);
		}
		return sl;
	}

	private void runClient() {
		try {
			writer = socket.getOutputStream();
			reader = socket.getInputStream();
			int readLength = 1024;
			byte[] bytes = new byte[readLength];
			int bytesRead = 0;
			byte[] receBytes = null;
			super.setConnected(socket.isConnected());
			Log.debug(super.readerName + " is running.Connected:"+ String.valueOf(super.isConnected));
			while (super.isConnected()) {
				if(reader != null){
					bytesRead = reader.read(bytes, 0, readLength);
					if (bytesRead > 0) {
						receBytes = new byte[bytesRead];
						System.arraycopy(bytes, 0, receBytes, 0, bytesRead);
						Log.debug("RXD:" + Util.convertByteArrayToHexString(receBytes));
						super.setBufferQueue(receBytes);
					} else {
						super.setConnected(false);
						Util.logAndTriggerApiErr(super.readerName, "FF22", Util.getErrorInfo("FF24"), LogType.Warn);
						break;
					}
				}
			}
		} catch (Exception e) {
			Util.logAndTriggerApiErr(super.readerName, "FF22", e.getMessage(), LogType.Warn);
			e.printStackTrace();
		} finally {
			try {
				Util.logAndTriggerApiErr(super.readerName, "FF22", "Socket be disconnecting!", LogType.Info);
				if (socket != null) {
					if(!socket.isClosed()){
						socket.close();
					}
				}
				if (writer != null) {
					writer.close();
				}
				if (reader != null) {
					reader.close();
					reader = null;
				}
				super.setConnected(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.debug(super.readerName + "Closed");
	}

	private void tcpSend(byte[] sendMsg) throws Exception {
		synchronized (lockObj) {
			writer.write(sendMsg, 0, sendMsg.length);// 发送
		}
	}

}
