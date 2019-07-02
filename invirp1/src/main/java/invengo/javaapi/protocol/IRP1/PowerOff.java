package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;
import invengo.javaapi.protocol.IRP1.MessageType;
import invengo.javaapi.protocol.IRP1.Reader;

public class PowerOff extends BaseMessage implements IEventHandle {

	public PowerOff() {
		super.onExecuting.add(this);
	}

	void PowerOff_OnExecuting(Object sender, EventArgs e) {
		Reader reader = (Reader) sender;
		reader.isStopReadTag = true;
		if (reader.readerType == "800") {
			super.msgType = MessageType.msgClass.get("PowerOff_800");
		} else {
			super.msgType = MessageType.msgClass.get("PowerOff_500");

		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {
		
	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		PowerOff_OnExecuting(sender, e);
		
	}
}
