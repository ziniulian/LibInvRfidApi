package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.ReceivedInfo;

/**
 * 国标盘存指令
 */
public class GBInventoryTag extends BaseMessage {

	private Reader currentReader;

	public GBInventoryTag() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 该指令用于标签盘存
	 * <p>
	 示例：
	 <p>GBInventoryTag message = new GBInventoryTag(antenna, target, session, condition);<br>
	 reader.send(message);<br>

	 <p>通过回调函数进行数据接收

	 public class XXX implements IMessageNotificationReceivedHandle{<br>
	 .<br>
	 .<br>
	 .<br>
	 <p>
	 public void messageNotificationReceivedHandle(BaseReader reader,IMessageNotification msg){<br>
	 if(msg instanceof GBInventoryTag){<br>
	 //do something.<br>
	 }<br>
	 }<br>
	 <p>
	 .<br>
	 .<br>
	 .<br>
	 }
	 *
	 * @param antenna 天线端口号
	 * @param target 目标,0~2
	 * @param session 会话,0~3
	 * @param condition 条件,0~3
	 */
	public GBInventoryTag(byte antenna, int target, int session, int condition) {
		super.isReturn = false;
		super.msgBody = new byte[2];
		super.msgBody[0] = antenna;

		String targetStr = "00";
		if(target == 0){
			targetStr = "00";
		}else if(target == 1){
			targetStr = "01";
		}else if(target == 2){
			targetStr = "10";
		}

		String sessionStr = "00";
		if(session == 0){
			sessionStr = "00";
		}else if(session == 1){
			sessionStr = "01";
		}else if(session == 2){
			sessionStr = "10";
		}else if(session == 3){
			sessionStr = "11";
		}

		String conditionStr = "00";
		if(condition == 0){
			conditionStr = "00";
		}else if(condition == 1){
			conditionStr = "01";
		}else if(condition == 2){
			conditionStr = "10";
		}else if(condition == 3){
			conditionStr = "11";
		}

		int inventoryCondition = Integer.parseInt("00" + conditionStr + sessionStr + targetStr, 2);
		super.msgBody[1] = (byte) inventoryCondition;

	}

	public void setCurrentReader(Reader currentReader) {
		this.currentReader = currentReader;
	}

	public InventoryReceivedInfo getReceivedMessage(){
		byte[] data = Decode.getRxMessageData(super.rxData);
		if(null == data){
			return null;
		}
		return new InventoryReceivedInfo(data, this.currentReader);
	}

	public class InventoryReceivedInfo extends ReceivedInfo{

		private Reader currentReader;
		public InventoryReceivedInfo(byte[] buff, Reader currentReader) {
			super(buff);
			this.currentReader = currentReader;
		}

		public byte getAntenna(){
			if(null != buff && buff.length > 1){
				return buff[0];
			}
			return 0x00;
		}

		private byte[] tagData = null;
		public byte[] getTagData(){
			if(null != currentReader){
				if(currentReader.isUtcEnable){//utc(8 bytes)
					if(null != buff && buff.length >= 10){
						tagData = new byte[buff.length - 9];
						System.arraycopy(buff, 1, tagData, 0, tagData.length);
					}
				}else {//no utc
					if(null != buff && buff.length >= 2){
						tagData = new byte[buff.length - 1];
						System.arraycopy(buff, 1, tagData, 0, tagData.length);
					}
				}
			}
			return tagData;
		}

		private byte[] utcData = null;
		public byte[] getUTC(){
			if(null != currentReader){
				if(currentReader.isUtcEnable){
					if(null != buff && buff.length >= 10){
						utcData = new byte[8];
						System.arraycopy(buff, (buff.length - 8), utcData, 0, 8);
					}
				}
			}
			return utcData;
		}
	}
}
