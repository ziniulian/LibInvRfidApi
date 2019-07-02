package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;
import invengo.javaapi.protocol.IRP1.MessageType;
import invengo.javaapi.protocol.IRP1.Reader;

public class PowerOn extends BaseMessage implements IEventHandle {

	public PowerOn(byte antenna) {
		super.msgBody = new byte[] { antenna };
		super.onExecuting.add(this);
	}

	void PowerOn_OnExecuting(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;
		reader.isStopReadTag = true;
		if (reader.readerType.equals("800")) {
			super.msgType = MessageType.msgClass.get("PowerOn_800");
		} else {
			super.msgType = MessageType.msgClass.get("PowerOn_500");
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		PowerOn_OnExecuting(sender, e);
		
	}
}
