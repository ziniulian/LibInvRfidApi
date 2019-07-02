package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/*
 * For Malaysia 807
 * Send file data to RFID reader
 * 指令长度	 命令字	参数类型	   数据序号       数据长度	  数据内容         CRC
 * (2字节)	 6EH	 1字节	  2字节	    1字节	  n字节	  (2字节)
 */
public class SendFileData extends BaseMessage implements IEventHandle {

	private byte paramType = 0x00;
	private byte[] dataId = new byte[2];
	private byte[] fileData;
	private int maxDataLen = 128;
	private int eventFlag = 0;//递归标识.fileData.length > 128:eventFlag = 1;fileData.length <= 128:eventFlag = 0.
	private static final byte[] END_DATA_ID = {(byte) 0xFF, (byte)0xFF};

	public SendFileData(byte[] fileData, byte[] dataId){
		if(null == fileData || null == dataId){
			throw new IllegalArgumentException();
		}

		int tempDataLen = fileData.length;
		this.dataId = dataId;
		/*
		 * 	新增结束指令：
		 *	参数类型		数据序号		数据长度		数据内容
		 *	0x00		0xFF 0xFF	0x01		0x00
		 */
		if(tempDataLen > maxDataLen){//fileData.length>maxDataLen=128,这里生成最后一次发送的数据-结束指令
			eventFlag = 1;
			this.fileData = fileData;

			super.msgBody = new byte[5];
			super.msgBody[0] = paramType;
			super.msgBody[1] = END_DATA_ID[0];
			super.msgBody[2] = END_DATA_ID[1];
			super.msgBody[3] = 0x01;
			super.msgBody[4] = 0x00;

			//			int loop = (tempDataLen / maxDataLen) & 0xFF;
			//			if (tempDataLen % maxDataLen == 0) {
			//				loop -= 1;
			//			}
			//
			//			super.msgBody[1] = (byte) (loop >> 8);
			//			super.msgBody[2] = (byte) (loop & 0xff);
			//			super.msgBody[3] = (byte) maxDataLen;
			//
			//			int startIndex = loop * maxDataLen;
			//			byte[] tempFileData = new byte[maxDataLen];
			//			System.arraycopy(fileData, startIndex, tempFileData, 0, fileData.length - startIndex);
			//
			//			System.arraycopy(tempFileData, 0, super.msgBody, 4, tempFileData.length);
			super.onExecuting.add(this);
		}else{//<= maxDataLen
			eventFlag = 0;

			super.msgBody = new byte[1 + this.dataId.length + 1 + maxDataLen];
			super.msgBody[0] = paramType;
			super.msgBody[1] = this.dataId[0];
			super.msgBody[2] = this.dataId[1];
			super.msgBody[3] = (byte) maxDataLen;

			byte[] tempFileData = new byte[maxDataLen];
			System.arraycopy(fileData, 0, tempFileData, 0, tempDataLen);
			System.arraycopy(tempFileData, 0, super.msgBody, 4, tempFileData.length);

		}
	}

	//For IV值, KEY值
	public SendFileData(byte[] data, byte[] dataId, byte type){
		if(null == data || null == dataId){
			throw new IllegalArgumentException();
		}
		int dataLen = data.length;

		super.msgBody = new byte[1 + this.dataId.length + 1 + dataLen];
		super.msgBody[0] = type;
		super.msgBody[1] = this.dataId[0];
		super.msgBody[2] = this.dataId[1];
		super.msgBody[3] = (byte) dataLen;

		System.arraycopy(data, 0, super.msgBody, 4, dataLen);
	}

	private void sendFileData_OnExecuting(Object sender, EventArgs e){
		BaseReader reader = (BaseReader) sender;
		int fileLen = this.fileData.length;
		//		int loop = (fileLen / maxDataLen) & 0xFF;
		//		if (fileLen % maxDataLen == 0) {
		//			loop -= 1;;
		//		}
		int loop = (fileLen / maxDataLen) & 0xFF;
		if (loop * maxDataLen != fileLen) {
			loop += 1;;
		}
		for (int i = 0; i < loop; i++) {
			byte[] temp = null;
			temp = new byte[maxDataLen];
			System.arraycopy(this.fileData, i * maxDataLen, temp, 0, maxDataLen);

			byte[] dataId = new byte[2];
			dataId[0] = (byte) (i >> 8);
			dataId[1] = (byte) (i & 0xff);

			SendFileData data = new SendFileData(temp, dataId);
			if (!reader.send(data)) {
				super.statusCode = data.statusCode;
				String key = String.format("%1$02X", super.statusCode);
				if (Util.getErrorInfo(key)!=null) {
					errInfo = Util.getErrorInfo(key);
				} else {
					errInfo = key;
				}
				throw new RuntimeException(super.errInfo);
			}
		}
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		switch (eventFlag) {
			case 0:
				//do nothing
				break;
			case 1:
				sendFileData_OnExecuting(sender, e);
				break;
			default:
				break;
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}
}
