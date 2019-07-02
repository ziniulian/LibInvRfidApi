package invengo.javaapi.protocol.IRP1;


/**
 * 开始读取条码数据-分离式手持机读条码指令
 */
public class ReadBarcode extends BaseMessage {

	public ReadBarcode() {
		// TODO Auto-generated constructor stub
		this(new byte[]{0x00});
	}

	public ReadBarcode(byte[] parametes){
		super.isReturn = false;
		super.msgType = MessageType.msgReadBarcode.get("BARCODE");
		super.msgBody = parametes;
	}

}
