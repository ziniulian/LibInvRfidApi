package invengo.javaapi.protocol.IRP1;

/**
 * 
 * 
 * @author dp732
 * 
 */
public class FirmwareUpgrading extends BaseMessage {

	public FirmwareUpgrading(int ptr, byte[] data) {
		super.msgBody = new byte[4 + data.length];
		super.msgBody[0] = (byte) ((ptr >> 24) & 0xff);
		super.msgBody[1] = (byte) ((ptr >> 16) & 0xff);
		super.msgBody[2] = (byte) ((ptr >> 8) & 0xff);
		super.msgBody[3] = (byte) (ptr & 0xff);
		System.arraycopy(data, 0, super.msgBody, 4, data.length);
	}
}
