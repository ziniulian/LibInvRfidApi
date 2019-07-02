package invengo.javaapi.core;

public class MessageInfo {

	public MessageInfo ev = this;
	public IMessage msg = null;
	public boolean isDone;

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public MessageInfo getEv() {
		return ev;
	}

	public void setEv(MessageInfo ev) {
		this.ev = ev;
	}

	public IMessage getMsg() {
		return msg;
	}

	public void setMsg(IMessage msg) {
		this.msg = msg;
	}

}
