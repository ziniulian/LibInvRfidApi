package invengo.javaapi.communication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import invengo.javaapi.core.ICommunication;

public class RS232 extends ICommunication {

    private Thread threadRun = null;// 监听线程
    private InputStream reader;
    private OutputStream writer;

    private SerialPort XCCom;
    private final Object lockObj = new Object();
    private String portName = "COM1";
    private int baudRate = 115200;
    private boolean isConn = false;

    @Override
    public boolean open(String connString) {
        String[] connStr = connString.split(",");
        if (connStr.length >= 1) {
            portName = connStr[0];
        }
        if (connStr.length >= 2) {
            baudRate = Integer.parseInt(connStr[1]);
        }
        if (connStr.length >= 2) {
            try {
                XCCom = new SerialPort(new File(this.portName), baudRate, 0);
                reader = XCCom.getInputStream();
                writer = XCCom.getOutputStream();
                this.isConn = true;
                super.setConnected(true);
            } catch (Exception e) {
                this.isConn = false;
                super.setConnected(false);
                e.printStackTrace();
            }
            if (threadRun == null || !threadRun.isAlive()) {
                threadRun = new Thread() {
                    @Override
                    public void run() {
                        runClient();
                    }
                };
                threadRun.start();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return super.isConnected();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void close() {
        super.setConnected(false);
        this.isConnected = false;
        try {
            this.XCCom.close();
        } catch (Exception e1) {

        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (threadRun != null) {
            if (threadRun.isAlive()) {
                threadRun.stop();
            }
            threadRun = null;
        }
    }

    @Override
    public int send(byte[] data) {
        int sl = 0;
        try {
            if (super.isConnected()) {
                //            	InvengoLog.i("Response", "INFO.Message Send - " + Util.convertByteArrayToHexString(data));
                comSend(data);// 发送
                sl = data.length;
            }
        } catch (Exception e) {
            sl = 0;
        }
        return sl;
    }

    private void runClient() {
        try {
            int readLength = 256;
            byte[] bytes = new byte[readLength];
            int bytesRead = 0;
            byte[] receBytes = null;
            super.setConnected(this.isConn);
            while (super.isConnected()) {
                if(reader.available() > 0){
                    bytesRead = reader.read(bytes, 0, readLength);
                    if (bytesRead > 0) {
                        receBytes = new byte[bytesRead];
                        //						System.out.println("+++++++" + Util.convertByteArrayToHexString(receBytes));
                        System.arraycopy(bytes, 0, receBytes, 0, bytesRead);
                        super.setBufferQueue(receBytes);
                    }
                }else{
                    Thread.sleep(5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != writer){
                    writer.close();
                }
                if(null != reader){
                    reader.close();
                }
                if (super.isConnected()) {
                    XCCom.close();
                }
                super.setConnected(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void comSend(byte[] sendMsg) {
        try {
            synchronized (lockObj) {
                writer.write(sendMsg, 0, sendMsg.length);// 发送
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
