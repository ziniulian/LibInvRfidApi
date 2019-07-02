package invengo.javaapi.core;

import java.io.Serializable;

public class ReceivedInfo implements Serializable {

	private static final long serialVersionUID = -5488214803215264262L;
	protected byte[] buff;
	
	public ReceivedInfo(byte[] buff){
		this.buff = buff;
	}
}
