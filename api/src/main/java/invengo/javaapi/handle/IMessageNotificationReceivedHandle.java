package invengo.javaapi.handle;

import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.BaseReader;

public interface IMessageNotificationReceivedHandle {

	void messageNotificationReceivedHandle(BaseReader reader,IMessageNotification msg);
	
}
