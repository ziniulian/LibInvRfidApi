package invengo.javaapi.protocol.IRP1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageType {

	public final static Map<String, Integer> msgClass = new HashMap<String, Integer>();
	public final static Map<Integer, String> msgType = new HashMap<Integer, String>();
	public final static Map<String, Integer> msgReadTag = new HashMap<String, Integer>();
	public final static Map<String, Integer> msgReadBarcode = new HashMap<String, Integer>();

	static {
		msgClass.put("TagOperationQuery_6C", Integer.valueOf("64", 16));
		msgClass.put("TagOperationConfig_6C", Integer.valueOf("65", 16));
		msgClass.put("QT_6C", Integer.valueOf("96", 16));
		msgClass.put("FastReadTIDConfig_EpcLength", Integer.valueOf("AC", 16));
		msgClass.put("FastReadTIDConfig_6C", Integer.valueOf("AD", 16));
		msgClass.put("ReadUnfixedTidConfig_6C", Integer.valueOf("EA", 16));
		msgClass.put("FixedTidLengthConfig_6C", Integer.valueOf("EB", 16));
		msgClass.put("ReadTagConfig_6C", Integer.valueOf("F4", 16));
		msgClass.put("ReadTagConfig_6B", Integer.valueOf("F5", 16));
		msgClass.put("IDFilter_6B", Integer.valueOf("A3", 16));
		msgClass.put("EasConfig_6C", Integer.valueOf("8C", 16));
		msgClass.put("EpcFilter_6C", Integer.valueOf("8E", 16));
		msgClass.put("FilterByTime", Integer.valueOf("8F", 16));
		msgClass.put("RXD_EPC_6C", Integer.valueOf("81", 16));
		msgClass.put("RXD_ID_6B", Integer.valueOf("43", 16));
		msgClass.put("RXD_TID_6C", Integer.valueOf("82", 16));
		msgClass.put("RXD_TID_6C_2", Integer.valueOf("D8", 16));
		msgClass.put("RXD_EPC_TID_UserData_6C", Integer.valueOf("90", 16));
		msgClass.put("FrequencyTableConfig_F6B", Integer.valueOf("9001", 16));
		msgClass.put("FrequencyTableReset_F6B", Integer.valueOf("1F", 16));
		msgClass.put("RXD_EPC_TID_UserData_6C_2", Integer.valueOf("97", 16));
		msgClass.put("RXD_EPC_TID_UserData_Reserved_6C", Integer.valueOf("F1", 16));
		msgClass.put("RXD_ID_UserData_6B", Integer.valueOf("AE", 16));
		msgClass.put("RXD_ID_UserData_6B_2", Integer.valueOf("F2", 16));
		msgClass.put("RXD_EPC_PC_6C", Integer.valueOf("91", 16));
		msgClass.put("SelectTag_6C", Integer.valueOf("80", 16));
		msgClass.put("WriteTag_6C", Integer.valueOf("99", 16));
		msgClass.put("WriteEpc", Integer.valueOf("83", 16));
		msgClass.put("ReadUserData_6C", Integer.valueOf("84", 16));
		msgClass.put("WriteUserData_6C", Integer.valueOf("85", 16));
		msgClass.put("BlockWrite_6C", Integer.valueOf("86", 16));
		msgClass.put("BlockErase_6C", Integer.valueOf("87", 16));
		msgClass.put("AccessPwdConfig_6C", Integer.valueOf("88", 16));
		msgClass.put("KillPwdConfig_6C", Integer.valueOf("89", 16));
		msgClass.put("LockMemoryBank_6C", Integer.valueOf("8A", 16));
		msgClass.put("BlockPermalock_6C", Integer.valueOf("98", 16));
		msgClass.put("KillTag_6C", Integer.valueOf("8B", 16));
		//	    msgClass.put("GetTagAccessPwd_6C", Integer.valueOf("92", 16));
		//	    msgClass.put("GetTagKillPwd_6C", Integer.valueOf("93", 16));
		msgClass.put("ReadUserData_6B", Integer.valueOf("44", 16));
		msgClass.put("WriteUserData_6B", Integer.valueOf("45", 16));
		msgClass.put("ReadUserData2_6B", Integer.valueOf("4B", 16));
		msgClass.put("WriteUserData2_6B", Integer.valueOf("4C", 16));
		msgClass.put("LockUserData_6B", Integer.valueOf("40", 16));
		msgClass.put("LockStateQuery_6B", Integer.valueOf("4E", 16));
		msgClass.put("PowerOn_800", Integer.valueOf("60", 16));
		msgClass.put("PowerOff_800", Integer.valueOf("61", 16));
		msgClass.put("SysQuery_800", Integer.valueOf("62", 16));
		msgClass.put("SysConfig_800", Integer.valueOf("63", 16));
		msgClass.put("Gpo_800", Integer.valueOf("D1", 16));
		msgClass.put("Gpi_800", Integer.valueOf("D2", 16));
		msgClass.put("RXD_IOTriggerSignal_800", Integer.valueOf("E4", 16));
		msgClass.put("TestModeConfig_800", Integer.valueOf("67", 16));
		msgClass.put("FirmwareUpgrade_800", Integer.valueOf("6B", 16));
		msgClass.put("ResetReader_800", Integer.valueOf("6C", 16));
		msgClass.put("SysCheck_800", Integer.valueOf("66", 16));
		msgClass.put("Keepalive", Integer.valueOf("E5", 16));
		msgClass.put("CustomBusinessData", Integer.valueOf("E6", 16));
		msgClass.put("CustomBusinessControl", Integer.valueOf("E7", 16));
		msgClass.put("ReaderVoltage", Integer.valueOf("A7", 16));
		msgClass.put("PowerOn_500", Integer.valueOf("41", 16));
		msgClass.put("PowerOff_500", Integer.valueOf("42", 16));
		msgClass.put("SysQuery_500", Integer.valueOf("46", 16));
		msgClass.put("SysConfig_500", Integer.valueOf("47", 16));
		msgClass.put("FreqConfig_500", Integer.valueOf("0910", 16));
		msgClass.put("FreqQuery_500", Integer.valueOf("0911", 16));
		msgClass.put("BaudRateMode_500", Integer.valueOf("4F", 16));
		msgClass.put("WorkModeSet_500", Integer.valueOf("A0", 16));
		msgClass.put("EnableDhcp_500", Integer.valueOf("D001", 16));
		msgClass.put("DisableDhcp_500", Integer.valueOf("D101", 16));
		msgClass.put("QueryDhcp_500", Integer.valueOf("D201", 16));
		msgClass.put("RssiLimitConfig_500", Integer.valueOf("A1", 16));
		msgClass.put("Buzzer_500", Integer.valueOf("A2", 16));
		msgClass.put("TemperatureAndHumidityQuery_500", Integer.valueOf("A4", 16));
		msgClass.put("AntennaInspect_500", Integer.valueOf("A5", 16));
		msgClass.put("IODevices_500", Integer.valueOf("A6", 16));
		msgClass.put("IODevices_502C", Integer.valueOf("4D", 16));
		msgClass.put("DataSentTime_500", Integer.valueOf("B3", 16));
		msgClass.put("RelayStartState_500", Integer.valueOf("B4", 16));
		msgClass.put("PowerParamConfig_500", Integer.valueOf("E8", 16));
		msgClass.put("ResetToFactoryDefault_500", Integer.valueOf("E9", 16));
		msgClass.put("ResetToFactoryDefault", Integer.valueOf("024A", 16));
		msgClass.put("DataSentMode_500", Integer.valueOf("B5", 16));

		msgClass.put("WhiteList_500", Integer.valueOf("B0", 16));
		msgClass.put("WhiteListDownLoad_500", Integer.valueOf("B1", 16));
		msgClass.put("WhiteListQuery_500", Integer.valueOf("B2", 16));
		msgClass.put("StartReaderAndReading_500", Integer.valueOf("CA", 16));
		msgClass.put("WiegandMode_500", Integer.valueOf("CB", 16));
		msgClass.put("ReadModeTrigger_500", Integer.valueOf("CC", 16));
		msgClass.put("ServerClientConfig_500", Integer.valueOf("D3", 16));
		msgClass.put("ServerClientQuery_500", Integer.valueOf("D4", 16));
		msgClass.put("PcIpsConfig_500", Integer.valueOf("D501", 16));
		msgClass.put("PcIpsQuery_500", Integer.valueOf("D6", 16));
		msgClass.put("PcSendTime_500", Integer.valueOf("D7", 16));
		msgClass.put("TagUpInterval_500", Integer.valueOf("DA01", 16));
		msgClass.put("ReaderInterval_500", Integer.valueOf("DB", 16));
		msgClass.put("ResetReader_500", Integer.valueOf("48", 16));

		msgClass.put("G2xmTagReadProtectionConfig", Integer.valueOf("94", 16));
		msgClass.put("G2xmTagReadProtectionCancel", Integer.valueOf("95", 16));
		msgClass.put("E2", Integer.valueOf("E2", 16));
		msgClass.put("ReaderBeep", Integer.valueOf("DC", 16));
		msgClass.put("HubComm", Integer.valueOf("EB01", 16));
		msgClass.put("FilterByTime_6C", Integer.valueOf("8F", 16));
		msgClass.put("FirmwareUpgrading", Integer.valueOf("091001", 16));
		msgClass.put("FirmwareUpgrading_ARM9", Integer.valueOf("6D", 16));
		msgClass.put("TemperatureQuery", Integer.valueOf("0913", 16));
		msgClass.put("RXD_EPC_TID_TEMPERATURE", Integer.valueOf("C1", 16));

		//分离式手持机新增指令
		//	    msgClass.put("HandsetModeSelect", Integer.valueOf("20", 16));
		//	    msgClass.put("HandsetPowerManager", Integer.valueOf("21", 16));
		//	    msgClass.put("HandsetBatteryManager", Integer.valueOf("22", 16));
		//	    msgClass.put("HandsetTriggerManager", Integer.valueOf("23", 16));
		msgClass.put("RXD_BARCODE", Integer.valueOf("B000", 16));
		msgClass.put("RXD_ReaderElectricQuantity", Integer.valueOf("B001", 16));
		msgClass.put("RXD_ReaderChargeStatus", Integer.valueOf("B003", 16));
		msgClass.put("RXD_VoltageAlarm", Integer.valueOf("B004", 16));
		msgClass.put("RXD_ReaderTriggerStatus", Integer.valueOf("B005", 16));
		//	    msgClass.put("StopReadBarcode", Integer.valueOf("26", 16));

		//国标指令
		msgClass.put("GBSelectTag", Integer.valueOf("20", 16));
		msgClass.put("GBInventoryTag", Integer.valueOf("21", 16));
		msgClass.put("GBAccessReadTag", Integer.valueOf("22", 16));
		msgClass.put("GBWriteTag", Integer.valueOf("23", 16));
		msgClass.put("GBEraseTag", Integer.valueOf("24", 16));
		msgClass.put("GBConfigTagLockOrSafeMode", Integer.valueOf("25", 16));
		msgClass.put("GBInactivateTag", Integer.valueOf("26", 16));
		msgClass.put("GBDynamicWriteTag", Integer.valueOf("27", 16));
		msgClass.put("GBCombinationReadTag", Integer.valueOf("28", 16));
		msgClass.put("GBReadAllBank", Integer.valueOf("29", 16));
		msgClass.put("RXD_EPC_TID_UserData_6C_PASSWORD", Integer.valueOf("C7", 16));
		msgClass.put("ReadReserved_6C", Integer.valueOf("C2", 16));

		msgClass.put("TagInactive", Integer.valueOf("B7", 16));
		msgClass.put("QueryTagPassword", Integer.valueOf("92", 16));
		msgClass.put("ConfigTagPassword", Integer.valueOf("93", 16));

		Set<String> set = msgClass.keySet();
		for (String key : set) {
			msgType.put((Integer)msgClass.get(key), key);
		}

		msgReadTag.put("EPC_6C", Integer.valueOf("81", 16));
		msgReadTag.put("TID_6C", Integer.valueOf("82", 16));
		msgReadTag.put("EPC_TID_UserData_6C", Integer.valueOf("90", 16));
		msgReadTag.put("EPC_TID_UserData_6C_2", Integer.valueOf("97", 16));
		msgReadTag.put("ID_6B", Integer.valueOf("43", 16));
		msgReadTag.put("ID_UserData_6B", Integer.valueOf("AE", 16));
		msgReadTag.put("EPC_6C_ID_6B", Integer.valueOf("31", 16));
		msgReadTag.put("TID_6C_ID_6B", Integer.valueOf("30", 16));
		msgReadTag.put("EPC_TID_UserData_6C_ID_UserData_6B", Integer.valueOf("32", 16));
		msgReadTag.put("EPC_TID_UserData_Reserved_6C_ID_UserData_6B", Integer.valueOf("F0", 16));
		msgReadTag.put("EPC_PC_6C", Integer.valueOf("91", 16));
		msgReadTag.put("EPC_TID_UserData_6C_PASSWORD", Integer.valueOf("C7", 16));
		msgReadTag.put("EPC_TID_TEMPERATURE", Integer.valueOf("C1", 16));

		msgReadBarcode.put("BARCODE", Integer.valueOf("B000", 16));
		msgReadBarcode.put("FirmwareUpgrade_ARM9", Integer.valueOf("B006", 16));
	}

}
