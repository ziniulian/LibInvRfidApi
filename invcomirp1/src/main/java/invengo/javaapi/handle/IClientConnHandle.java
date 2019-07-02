package invengo.javaapi.handle;

import java.net.Socket;

public interface IClientConnHandle {

	void clientConnHandle(Socket socket,String pVer);
}
