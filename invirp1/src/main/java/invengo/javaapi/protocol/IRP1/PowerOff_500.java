package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;
import invengo.javaapi.protocol.IRP1.BaseMessage;
import invengo.javaapi.protocol.IRP1.Reader;

/**
 * 500系列关功放
 *
 * @author dp732
 *
 */
class PowerOff_500 extends BaseMessage implements IEventHandle {

	public PowerOff_500() {
		super.onExecuting.add(this);
	}


	void PowerOff_500_OnExecuting(Object sender, EventArgs e) {
	}

	public void eventHandle_executed(Object sender, EventArgs e) {

	}


	public void eventHandle_executing(Object sender, EventArgs e) {
		if (isReturn){
			((Reader)sender).isStopReadTag = true;
		}
	}
}
