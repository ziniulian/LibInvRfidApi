package invengo.javaapi.handle;

import invengo.javaapi.core.BaseReader;

public interface IBufferReceivedHandle {
	void bufferReceived(BaseReader reader, byte[] e);
}
