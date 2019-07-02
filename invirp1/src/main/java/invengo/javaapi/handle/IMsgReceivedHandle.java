package invengo.javaapi.handle;

import invengo.javaapi.core.IMessageNotification;

public interface IMsgReceivedHandle {

	void bufferReceivedHandle(IMessageNotification e);
}
