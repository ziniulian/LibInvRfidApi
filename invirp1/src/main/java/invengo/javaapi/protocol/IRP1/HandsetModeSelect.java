package invengo.javaapi.protocol.IRP1;

/**
 * 分离式手持机功能模块选择指令
 */
public class HandsetModeSelect extends BaseMessage {

	private static final byte RFID = 0x00;
	private static final byte BARCODE = 0x01;
	private static final byte BOTH = 0x02;

	public HandsetModeSelect() {
		//
	}

	/**
	 * @param mode	功能模块
	 * @param command	开关指令,0x01
	 */
	public HandsetModeSelect(Mode mode, int command){
		super.msgBody = new byte[2];
		if(mode == Mode.RFID_MODE){
			super.msgBody[0] = RFID;
		}else if(mode == Mode.BARCODE_MODE){
			super.msgBody[0] = BARCODE;
		}else if(mode == Mode.BOTH_MODE){
			super.msgBody[0] = BOTH;
		}

		super.msgBody[1] = (byte) command;
	}

	public enum Mode{
		RFID_MODE, BARCODE_MODE, BOTH_MODE
	}

}
