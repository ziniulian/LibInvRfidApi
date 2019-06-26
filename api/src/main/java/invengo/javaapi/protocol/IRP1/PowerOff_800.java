package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

/**
 * 800系列关功放
 *
 * @author dp732
 *
 */
public class PowerOff_800 extends BaseMessage implements IEventHandle {

	public PowerOff_800() {
		super.onExecuting.add(this);
	}

	void PowerOff_800_OnExecuting(Object sender, EventArgs e) {
		if (isReturn) {
			((Reader) sender).isStopReadTag = true;
		}
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}

	public void eventHandle_executing(Object sender, EventArgs e) {
		PowerOff_800_OnExecuting(sender, e);

	}
}
