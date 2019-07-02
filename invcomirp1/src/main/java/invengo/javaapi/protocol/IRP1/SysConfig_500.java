package invengo.javaapi.protocol.IRP1;

/**
 * 配置读写器系统配置指令
 *
 * @author dp732
 *
 */
public class SysConfig_500 extends BaseMessage {

    /**
     * @param infoType 系统配置信息类型
     * @param infoLength 系统配置信息类型长度
     * @param pData 配置数据
     */
    public SysConfig_500(byte infoType, byte infoLength, byte[] pData)
    {
        super.msgBody = new byte[2 + pData.length];
        super.msgBody[0] = infoType;
        super.msgBody[1] = infoLength;
        System.arraycopy(pData, 0, msgBody, 2, pData.length);
    }
    public SysConfig_500() { }
}
