package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.GBMemoryBank;
import invengo.javaapi.core.Util;

/**
 * 配置标签存储区属性(锁)或安全模式
 */
public class GBConfigTagLockOrSafeMode extends BaseMessage {

	public GBConfigTagLockOrSafeMode() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 该构造函数用于配置标签存储区域属性-锁指令<br>
	 * <p>
	 * 注意:<br>
	 * 1.bank为GBMemoryBank.GBTidMemory时,action仅设置为LockAction.Read_Only_GB,LockAction.No_Read_Write_GB时指令才可能正常设置;
	 * <p>
	 * 2.bank为GBMemoryBank.GBEPCMemory时,action仅设置为LockAction.Read_Write_GB,LockAction.Read_Only_GB时指令才可能正常设置;
	 * <p>
	 * 3.bank为GBMemoryBank.GBReservedMemory时,action仅设置为LockAction.Write_Only_GB,LockAction.No_Read_Write_GB时指令才可能正常设置;
	 * <p>
	 * 4.bank为GBMemoryBank.GBUser1Memory...GBUser16Memory时,action无限制.<br>
	 *
	 * <p>
	 用法:<br>
	 GBSelectTag selectTagMessage = new GBSelectTag(bank, target, rule, headAddress, data);<br>
	 if(reader.send(selectTagMessage)){<br>
	 GBConfigTagLockOrSafeMode message = new GBConfigTagLockOrSafeMode(antenna, password, bank, action);<br>
	 reader.send(message);}<br>
	 *
	 *
	 * @param antenna	天线号
	 * @param password	标签锁密码
	 * @param bank	存储区域
	 * @param action	锁动作
	 */
	public GBConfigTagLockOrSafeMode(int antenna, String password, GBMemoryBank bank, LockAction action) {
		int configAction = -1;
		if(action == LockAction.Read_Write_GB){
			configAction = 0;
		}else if(action == LockAction.Read_Only_GB){
			configAction = 1;
		}else if(action == LockAction.Write_Only_GB){
			configAction = 2;
		}else if(action == LockAction.No_Read_Write_GB){
			configAction = 3;
		}

		byte[] passwordByte = Util.convertHexStringToByteArray(password);

		super.msgBody = new byte[8];

		super.msgBody[0] = (byte) antenna;
		System.arraycopy(passwordByte, 0, super.msgBody, 1, passwordByte.length);
		super.msgBody[5] = bank.getValue();
		super.msgBody[6] = 0x00;
		super.msgBody[7] = (byte) configAction ;
	}


	public enum LockAction{
		Read_Write_GB, Read_Only_GB, Write_Only_GB, No_Read_Write_GB
	}

	/**
	 * 该构造函数用于配置标签存储区域安全模式<br>
	 *
	 * <p>
	 用法:<br>
	 GBSelectTag selectTagMessage = new GBSelectTag(bank, target, rule, headAddress, data);<br>
	 if(reader.send(selectTagMessage)){<br>
	 GBConfigTagLockOrSafeMode message = new GBConfigTagLockOrSafeMode(antenna, password, bank, mode);<br>
	 reader.send(message);}<br>
	 *
	 *
	 * @param antenna	天线号
	 * @param password	标签锁密码
	 * @param bank	存储区域
	 * @param mode	安全模式,值为0~3
	 */
	public GBConfigTagLockOrSafeMode(int antenna, String password, GBMemoryBank bank, int mode) {
		byte[] passwordByte = Util.convertHexStringToByteArray(password);

		super.msgBody = new byte[8];

		super.msgBody[0] = (byte) antenna;
		System.arraycopy(passwordByte, 0, super.msgBody, 1, passwordByte.length);
		super.msgBody[5] = bank.getValue();
		super.msgBody[6] = 0x01;
		super.msgBody[7] = (byte) mode ;
	}
}
