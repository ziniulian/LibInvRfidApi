package invengo.javaapi.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import invengo.javaapi.core.ICommunication;

public class RS232 extends ICommunication {

    private Thread threadRun = null;// 监听线程
    private InputStream reader;
    private OutputStream writer;

    private SerialPort XCCom;
    private final Object lockObj = new Object();
    private String portName = "COM1";
    private int baudRate = 115200;
    private int parity = SerialPort.PARITY_NONE;
    private int dataBits = 8;
    private int stopBits = SerialPort.STOPBITS_1;
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
        if (connStr.length >= 3) {
            String parityString = connStr[2];
            if (parityString.equals("None")) {
                parity = SerialPort.PARITY_NONE;
            } else if (parityString.equals("Odd")) {
                parity = SerialPort.PARITY_ODD;
            } else if (parityString.equals("Even")) {
                parity = SerialPort.PARITY_EVEN;
            } else if (parityString.equals("Mark")) {
                parity = SerialPort.PARITY_MARK;
            } else if (parityString.equals("Space")) {
                parity = SerialPort.PARITY_SPACE;
            }
        }
        if (connStr.length >= 4) {
            dataBits = Integer.parseInt(connStr[3]);
        }
        if (connStr.length >= 5) {
            String stopBitsString = connStr[4];
            if (stopBitsString.equals("None")) {
                stopBits = 0;
            } else if (stopBitsString.equals("1")) {
                stopBits = SerialPort.STOPBITS_1;
            } else if (stopBitsString.equals("1.5")) {
                stopBits = SerialPort.STOPBITS_1_5;
            } else if (stopBitsString.equals("2")) {
                stopBits = SerialPort.STOPBITS_2;
            }
        }
        System.setProperty("java.library.path", ".");
        CommPortIdentifier portId = null;
        if (connStr.length >= 2) {
            try {
                portId = CommPortIdentifier.getPortIdentifier(portName);
                XCCom = (SerialPort) portId.open("Serial_Communication", 2000);
                XCCom.notifyOnDataAvailable(true);
                XCCom.setSerialPortParams(baudRate, dataBits, stopBits, parity);
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
                if(reader.available() > 0)
                {
                    bytesRead = reader.read(bytes, 0, readLength);
                    if (bytesRead > 0) {
                        receBytes = new byte[bytesRead];
                        System.arraycopy(bytes, 0, receBytes, 0, bytesRead);
                        super.setBufferQueue(receBytes);
                    }

                }else
                {
                    Thread.sleep(5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                reader.close();
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
