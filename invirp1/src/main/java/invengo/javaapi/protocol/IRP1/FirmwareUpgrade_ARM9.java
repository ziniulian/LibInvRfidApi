package invengo.javaapi.protocol.IRP1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

public class FirmwareUpgrade_ARM9 extends BaseMessage implements IEventHandle {

	private static final int LEN = 128;
	private FileInputStream fis = null;

	public FirmwareUpgrade_ARM9() {
		super.isReturn = false;
		super.msgType = MessageType.msgReadBarcode.get("FirmwareUpgrade_ARM9");
	}

	private String fileName = "";
	public FirmwareUpgrade_ARM9(String fileName){
		this.fileName = fileName;

		super.msgType = 0x6D;// 结束帧命令字
		super.msgBody = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, 0x00, 0x00, 0x00, 0x00 };// 结束帧内容
		super.onExecuting.add(this);
		super.onExecuted.add(this);
	}

	@Override
	public void eventHandle_executing(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;

		FirmwareUpgrade_ARM9 msg = new FirmwareUpgrade_ARM9();
		if (!reader.send(msg)) {
			throw new RuntimeException("Message sending failed.");
		}

		byte[] data = null;
		try {
			Thread.sleep(1000);

			//文件头
			byte[] info = new byte[19];
			//文件类型
			info[0] = 0x00;
			//升级密钥
			info[1] = (byte) 0xa5;
			info[2] = (byte) 0xc3;
			//升级版本
			String v = this.fileName.substring(fileName.indexOf("svn"), fileName.indexOf("_")).toLowerCase();
			byte[] vs = v.getBytes("ASCII");
			System.arraycopy(vs, 0, info, 3, 6);

			fis = new FileInputStream(fileName);
			int count = fis.available();
			byte[] bs = new byte[count];
			fis.read(bs, 0, count);
			fis = null;
			int fc = bs.length / LEN;
			if (bs.length % LEN > 0){
				fc++;
			}
			//总帧数
			info[9] = (byte)((fc >> 24) & 0xff);
			info[10] = (byte)((fc >> 16) & 0xff);
			info[11] = (byte)((fc >> 8) & 0xff);
			info[12] = (byte)(fc & 0xff);
			// 升级包大小
			info[13] = (byte)((count >> 24) & 0xff);
			info[14] = (byte)((count >> 16) & 0xff);
			info[15] = (byte)((count >> 8) & 0xff);
			info[16] = (byte)(count & 0xff);
			byte[] fileCrc = CRCClass.getCRC16(bs);
			// crc
			info[17] = fileCrc[0];
			info[18] = fileCrc[1];
			FirmwareUpgrading_ARM9 message = new FirmwareUpgrading_ARM9(0, info);
			if (!reader.send(message, 2000)){
				throw new RuntimeException("Message sending failed.");
			}

			//	        fis = new FileInputStream(fileName);
			//	        int count = fis.available();
			int p = 0;
			int bc = 1;
			do {
				if (count >= 128) {
					data = new byte[128];
				} else {
					data = new byte[count];
				}
				//				fis.read(data, 0, data.length);
				System.arraycopy(bs, p, data, 0, data.length);
				for (int i = 0; i < 3; i++) {
					FirmwareUpgrading_ARM9 msgUp = new FirmwareUpgrading_ARM9(bc, data);
					if (reader.send(msgUp)) {
						p += data.length;
						count -= data.length;
						bc++;
						break;
					} else {
						if (i == 3) {
							super.statusCode = msgUp.getStatusCode();
							throw new RuntimeException(
									"Data transmission failure.");
						}
					}

				}
			} while (count > 0);
		} catch (FileNotFoundException ioEx) {
			System.out.println(ioEx.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ee) {
			ee.printStackTrace();
		}
	}

	@Override
	public void eventHandle_executed(Object sender, EventArgs e) {
		//		FirmwareUpgrade_800_OnExecuted(sender, e);
	}

	void FirmwareUpgrade_800_OnExecuted(Object sender, EventArgs e) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ResetReader_800 msg = new ResetReader_800();
		Reader reader = (Reader) sender;
		reader.send(msg);
	}


}
