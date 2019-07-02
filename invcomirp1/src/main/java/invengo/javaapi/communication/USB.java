package invengo.javaapi.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ch.ntb.usb.Device;
import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.USBException;
import invengo.javaapi.core.ICommunication;

public class USB extends ICommunication {

	private Thread threadRun = null;// 监听线程
	private final Object lockObj = new Object();

	private Device usbDevice;
	private InputStream reader;
	private OutputStream writer;

	private boolean isConn = false;

	@SuppressWarnings("deprecation")
	public void close() {
		super.setConnected(false);
		this.isConnected = false;
		if (usbDevice != null) {
			try {
				usbDevice.close();
				ch.ntb.usb.USB.unregisterDevice(usbDevice);
			} catch (USBException e) {
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (threadRun != null) {
			if (threadRun.isAlive()) {
				threadRun.stop();
			}
			threadRun = null;
		}
	}

	public boolean open(String connString) {
		synchronized (this) {
			try {
				LibusbJava.usb_init();
				usbDevice = ch.ntb.usb.USB.getDevice((short) 0x8086,
						(short) 0xFEED);
				System.out.println(usbDevice.getIdVendor());
				usbDevice.updateDescriptors();
				System.out.println("usbDevice.getConfigDescriptors() != null");
				usbDevice.open(1, 0, -1);
				reader = new UsbInputStream(usbDevice);
				writer = new UsbOutputStream(usbDevice);
				this.isConn = true;
				super.setConnected(true);
			} catch (Exception e) {
				this.isConn = false;
				super.setConnected(false);
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
				e.printStackTrace();
			}
			return super.isConnected();
		}

	}

	public int send(byte[] data) {
		int sl = 0;
		try {
			if (super.isConnected()) {
				usbSend(data);// 发送
				sl = data.length;
			}
		} catch (Exception e) {
			sl = 0;
		}
		return sl;
	}

	private void runClient() {
		try {
			int readLength = 19;
			byte[] bytes = new byte[readLength];
			int bytesRead = 0;
			byte[] receBytes = null;
			super.setConnected(this.isConn);
			while (super.isConnected()) {
				bytesRead = reader.read(bytes, 0, readLength);
				if (bytesRead > 0) {
					receBytes = new byte[bytesRead];
					System.arraycopy(bytes, 0, receBytes, 0, bytesRead);
					super.setBufferQueue(receBytes);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				reader.close();
				if (super.isConnected()) {
					usbDevice.close();
				}
				super.setConnected(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void usbSend(byte[] sendMsg) {
		try {
			synchronized (lockObj) {
				writer.write(sendMsg, 0, sendMsg.length);// 发送
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

/**
 * usb输入流
 *
 * @author zxl672
 *
 */
class UsbInputStream extends InputStream {
	/**
	 * 输入管道
	 */
	private ch.ntb.usb.Device device;
	/**
	 * 输入缓冲
	 */
	private List<Integer> list = new ArrayList<Integer>();

	/**
	 * 构造函数
	 *
	 * @param inPipe
	 */
	public UsbInputStream(ch.ntb.usb.Device device) {
		this.device = device;
	}

	/**
	 * 读单个字节
	 */
	public int read() throws IOException {
		// 如果缓冲区没有了，循环去取直到有数据
		while (list.size() <= 0) {
			// 取得数据放入缓冲区
			readList(list);
			try {
				// 暂停2毫秒
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		// 取得第一个
		Integer rs = (Integer) list.get(0);
		// 去掉第一个
		list.remove(0);
		// 返回
		return rs.intValue();

	}

	/**
	 * 取数据
	 *
	 * @param list
	 */
	private void readList(List<Integer> list) {
		try {
			byte[] buffer = new byte[256];
			int dataLength = device.readBulk(0x82, buffer, buffer.length, 1000,
					false);
			int i = 0;
			while (i < dataLength) {
				list.add(new Integer(buffer[i]));
				i++;
			}
		} catch (Exception e) {
		}
	}
}

/**
 * usb输出流
 *
 * @author zxl672
 *
 */
class UsbOutputStream extends OutputStream {

	private ch.ntb.usb.Device device;

	/**
	 * 构造函数
	 *
	 * @param outPipe
	 */
	public UsbOutputStream(ch.ntb.usb.Device device) {
		this.device = device;
	}

	/**
	 * 缓冲区
	 */
	private List<Integer> list = new ArrayList<Integer>();

	/**
	 * 写单个字节
	 */
	public void write(int b) throws IOException {
		synchronized (list) {
			list.add(new Integer(b));
		}

	}

	/**
	 * 写多个字节
	 */
	public void write(byte b[]) throws IOException {
		synchronized (list) {
			for (int i = 0; i < b.length; i++) {
				list.add(new Integer(b[i]));
			}
		}
	}

	/**
	 * 真正输出到usb上
	 */
	public void flush() throws IOException {
		synchronized (list) {
			try {
				byte[] send = new byte[list.size()];
				for (int i = 0; i < send.length; i++) {
					Integer b = (Integer) list.get(i);
					send[i] = (byte) b.intValue();
				}
				device.writeBulk(0x01, send, send.length, 1000, false);
				list.removeAll(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
