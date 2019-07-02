package invengo.javaapi.protocol.IRP1;

/**
 * 读写器系统参数校调指令
 *
 * @author dp732
 *
 */
public class SysCheck_800 extends BaseMessage {

    /**
     * @param parameter 读写器系统参数校调指令的校调类型（Sys_Check）
     * @param pData 校调参数
     */
    public SysCheck_800(Byte parameter, Byte[] pData)
    {
        //指令内容
        super.msgBody = new byte[pData.length + 1];
        super.msgBody[0] = parameter;//校调参数类型
        System.arraycopy(pData, 0, msgBody, 1, pData.length);
    }
    public SysCheck_800() { }
}
