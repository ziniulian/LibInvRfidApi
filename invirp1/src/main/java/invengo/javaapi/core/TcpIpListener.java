package invengo.javaapi.core;

import invengo.javaapi.handle.IClientConnHandle;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TcpIpListener {
	public List<IClientConnHandle> onClientConnHandle = new ArrayList();
	int port;
	ServerSocket server = null;
	volatile boolean isStop = false;
	String pVer;

	public TcpIpListener(int port, String protocol) {
		this.port = port;
		this.pVer = protocol;
		try {
			this.server = new ServerSocket(port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		Thread thread = new Thread() {
			public void run() {
				TcpIpListener.this.Start();
			}
		};
		thread.start();
	}

	private void Start() {
		try {
			this.isStop = false;
			while (!(this.isStop)) {
				Socket socket = this.server.accept();
				if (this.onClientConnHandle != null)
					for (int i = 0; i < this.onClientConnHandle.size(); ++i)
						((IClientConnHandle) this.onClientConnHandle.get(i))
								.clientConnHandle(socket, this.pVer);
			}
		} catch (IOException e) {
			Util.logAndTriggerApiErr("Port:" + this.port, "", e.getMessage(),
					Util.LogType.Error);
		} finally {
			this.isStop = true;
			try {
				this.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		this.isStop = true;
		if (this.server == null)
			return;
		try {
			this.server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}