package invengo.javaapi.protocol.IRP1;

/**
 * 500系列模式设置（原始或衍生）指令
 *
 * @author dp732
 *
 */
public class BaudRateMode_500 extends BaseMessage {

    /**
     * @param infoType 信息类型:原始(0x00)或衍生(0x01)
     */
    public BaudRateMode_500(byte infoType)
    {
        super.msgBody = new byte[] { infoType };
    }
    public BaudRateMode_500() { }
}
