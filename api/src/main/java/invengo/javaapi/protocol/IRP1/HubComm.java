package invengo.javaapi.protocol.IRP1;

/**
 * 读写器与hub通信设置
 *
 * @author dp732
 *
 */
public class HubComm extends BaseMessage {

	/**
	 * @param hubNum Hub号,01H~04H
	 * @param antennaNum Hub内天线端口,自然数表示天线号
	 */
	public HubComm(byte hubNum,byte antennaNum){
		super.isReturn = false;
		super.msgBody = new byte[3];
		super.msgBody[0] = 0x02;
		super.msgBody[1] = hubNum;
		super.msgBody[2] = antennaNum;
	}

	public HubComm(){

	}
}
