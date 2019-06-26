package invengo.javaapi.protocol.IRP1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 读写器基带升级指令
 *
 * @author dp732
 *
 */
public class FirmwareUpgrade_800 extends BaseMessage implements IEventHandle {
	String fileName = "";

	public FirmwareUpgrade_800() {
		super.isReturn = false;
	}

	public FirmwareUpgrade_800(String fileName) {
		this.fileName = fileName;
		File file = new File(fileName);
		if (!file.exists()) {
			throw new RuntimeException("The file does not exist.");
		}
		super.msgType = 0x0910;// 结束帧命令字
		super.msgBody = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, 0x00, 0x00, 0x00, 0x00 };// 结束帧内容
		super.onExecuting.add(this);
		super.onExecuted.add(this);
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

	void FirmwareUpgrade_800_OnExecuting(Object sender, EventArgs e) {

		Reader reader = (Reader) sender;

		FirmwareUpgrade_800 msg = new FirmwareUpgrade_800();
		if (!reader.send(msg)) {
			throw new RuntimeException("Message sending failed.");
		}

		byte[] data = null;
		try {
			FileInputStream fs = new FileInputStream(fileName);
			int fl = fs.available();
			int p = 0;
			int bc = 1;
			do {
				if (fl >= 128) {
					data = new byte[128];
				} else {
					data = new byte[fl];
				}
				fs.read(data, 0, data.length);
				for (int i = 0; i < 3; i++) {
					FirmwareUpgrading msgUp = new FirmwareUpgrading(bc, data);
					if (reader.send(msgUp)) {
						p += data.length;
						fl -= data.length;
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
			} while (fl > 0);
		} catch (FileNotFoundException ioEx) {
			System.out.println(ioEx.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		FirmwareUpgrade_800_OnExecuted(sender, e);
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		FirmwareUpgrade_800_OnExecuting(sender, e);
	}
}
