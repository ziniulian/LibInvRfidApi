package invengo.javaapi.protocol.IRP1;

import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.EventArgs;
import invengo.javaapi.handle.IEventHandle;

public class TagInactive extends BaseMessage implements IEventHandle {

	public TagInactive() {
		// TODO Auto-generated constructor stub
	}
	
	public TagInactive(byte antenna, byte[] password){
		super.msgBody = new byte[1 + password.length];
		super.msgBody[0] = antenna;
		System.arraycopy(password, 0, super.msgBody, 1, password.length);
	}
	
	public TagInactive(byte antenna, byte[] password, byte[] tagID, MemoryBank tagIDType){
		this(antenna, password);
		this.tagID = tagID;
		this.tagIDType = tagIDType;
		super.onExecuting.add(this);
	}
	
	@Override
	public void eventHandle_executed(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

	@Override
	public void eventHandle_executing(Object sender, EventArgs e) {
		super.selectTag(sender, e);
	}

}
