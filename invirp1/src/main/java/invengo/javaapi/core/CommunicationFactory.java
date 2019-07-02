package invengo.javaapi.core;

import java.net.Socket;

import android.app.Activity;

public class CommunicationFactory {

	public static ICommunication createCommunication(String connClassName) {
		ICommunication myXCRF = null;
		try {
			myXCRF = (ICommunication) Class.forName(
					"invengo.javaapi.communication." + connClassName)
					.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return myXCRF;
	}
	
	 public static ICommunication createCommunication(String connClassName,Socket socket)
     {
//         ICommunication myXCRF = null;
//         try
//         {
//            Class c = Class.forName(
//					"invengo.javaapi.communication." + connClassName);
//            Constructor constructor = c.getConstructor(Socket.class);
//            myXCRF = (ICommunication) constructor.newInstance(socket);
//         }
//         catch (Exception e)
//         {
//             throw new RuntimeException(e);
//         }
//         return myXCRF;
		 ICommunication iConn = createCommunication(connClassName);
		 iConn.setSocket(socket);
		 return iConn;
     }
	 
	 /**
	  * BLE
	  */
	public static ICommunication createCommunication(String connClassName, Activity context){
		 ICommunication iConn = createCommunication(connClassName);
		 iConn.setContext(context);
		 return iConn;
	 }
}
