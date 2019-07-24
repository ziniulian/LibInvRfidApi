package invengo.javaapi.protocol.IRP1;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.IProcess;
import invengo.javaapi.core.Log;
import invengo.javaapi.core.Util;

public class Decode implements IProcess {

	private byte[] recvbuff;
	private String portType;
	List<Byte> buffList = new ArrayList<Byte>();
	volatile boolean is56 = false;

	public IMessage getConnectMessage() {
		return null;
		//		return new Gpi_800((byte) 0x00);
	}

	public IMessage getDisconnectMessage() {
		return new Gpi_800((byte) 0x00);
		//		return new PowerOff_800();
	}

	public String getPortType() {
		return this.portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public IMessageNotification parseMessageNoticefaction(byte[] recvMsg) {
		IMessageNotification msg = null;
		//		int id = getRxMessageID(recvMsg) & 0xFF;
		int id = getRxMessageID(recvMsg);
		if (MessageType.msgType.containsKey(id)) {
			try {
				msg = (IMessageNotification) Class.forName("invengo.javaapi.protocol.IRP1." + MessageType.msgType.get(id)).newInstance();
				msg.setReceivedData(recvMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	public void parse(byte[] buff, List<byte[]> msgs) {
		if (buff == null) {
			return;
		}
		byte[] m_recvBytes = null;// 数据处理中间变量
		if (this.portType.equals("RS232") || this.portType.equals("USB")
				|| this.portType.equals("Bluetooth")
				|| this.portType.equals("BluetoothLE")
				|| this.portType.equals("BluetoothLET")) {
			if (buff.length > 0) {
				buff = desFormatData(buff);
			}
		}
		if (this.recvbuff == null) {
			this.recvbuff = buff;
		} else {
			byte[] bs = new byte[buff.length + this.recvbuff.length];
			System.arraycopy(this.recvbuff, 0, bs, 0, this.recvbuff.length);
			System.arraycopy(buff, 0, bs, this.recvbuff.length, buff.length);
			this.recvbuff = bs;
		}

		if (this.recvbuff.length < 5) {
			return;
		} else {
			m_recvBytes = this.recvbuff;
			this.recvbuff = null;// 缓存清空
		}
		{
			// 循环处理接收到的数据
			int i = 0;
			int orderLength = 0;// 指令长度，找到第一个is00就算长度
			int lostDataStartIndex = 0;
			while (i < m_recvBytes.length) {
				// 查找到帧头（第一个00H）
				if (m_recvBytes[i] <= 5)// 查找到第一个00H
				{
					// 指令长度小于5则继续接收
					if (i <= m_recvBytes.length - 5)// 算长度
					{
						orderLength = m_recvBytes[i + 1] & 0xff;
						if (m_recvBytes[i] > 0) {
							orderLength = m_recvBytes[i] << 8 | m_recvBytes[i + 1] & 0xFF;
						}
					} else {
						this.recvbuff = new byte[m_recvBytes.length - i];
						System.arraycopy(m_recvBytes, i, recvbuff, 0, recvbuff.length);
						if (i != lostDataStartIndex) {
							byte[] lostData = new byte[i - lostDataStartIndex];
							System.arraycopy(m_recvBytes, lostDataStartIndex, lostData, 0, lostData.length);
							Log.debug("丢失的数据1:" + Util.convertByteArrayToHexString(lostData));
						}
						break;
					}
					// 针对指令长度做相应的处理
					if (orderLength == 0) {
						i++;
						continue;
					}
					// 根据真实的指令长度判断是否够一条数据，够则处理，不够则继续接收
					if (orderLength + 4 <= m_recvBytes.length - i) {
						byte[] m_buff = new byte[4 + orderLength];
						System.arraycopy(m_recvBytes, i, m_buff, 0, m_buff.length);
						if (CRCClass.validateCRC16(m_buff)) {
							msgs.add(m_buff);// 返回消息数据
							i += m_buff.length;// 设置i的位置
							lostDataStartIndex = i;
						} else {
							boolean is00 = false;
							for (int k = i + 1; k < 4 + orderLength + i; k++) {
								if (m_recvBytes[k] <= 0x05) {
									if (k + 1 < 4 + orderLength + i && m_recvBytes[k] == 0 && m_recvBytes[k + 1] == 0) {
										continue;
									}
									is00 = true;
									byte[] lostData = new byte[k - lostDataStartIndex];
									System.arraycopy(m_recvBytes, lostDataStartIndex, lostData, 0, lostData.length);
									Log.debug("丢失的数据2:" + Util.convertByteArrayToHexString(lostData));
									i = k;
									lostDataStartIndex = k;
									break;
								}
							}

							if (!is00) {
								byte[] lostData = new byte[m_recvBytes.length - lostDataStartIndex];
								System.arraycopy(m_recvBytes, lostDataStartIndex, lostData, 0, lostData.length);
								Log.debug("丢失的数据3:" + Util.convertByteArrayToHexString(lostData));
								lostDataStartIndex = i = 4 + orderLength + i;
							}
						}
					} else {
						this.recvbuff = new byte[m_recvBytes.length - i];
						System.arraycopy(m_recvBytes, i, recvbuff, 0, recvbuff.length);
						if (orderLength > 50 && this.recvbuff.length >= 7) {
							boolean isC = false;
							for (int ii = 1; ii < this.recvbuff.length; ii++) {
								if (this.recvbuff[ii] <= 0x05) {
									int iiorderLength = 0;
									if (ii <= recvbuff.length - 5)// 算长度
									{
										iiorderLength = recvbuff[ii + 1] & 0xff;
										if (recvbuff[ii] > 0) {
											iiorderLength = recvbuff[ii] << 8 | recvbuff[ii + 1] & 0xFF;
										}
									}

									if (iiorderLength != 0 && iiorderLength + 4 <= recvbuff.length - ii) {
										byte[] iim_buff = new byte[4 + iiorderLength];
										System.arraycopy(recvbuff, ii, iim_buff, 0, iim_buff.length);
										if (CRCClass.validateCRC16(iim_buff)) {
											msgs.add(iim_buff);// 返回消息数据
											i = i + ii + iim_buff.length;// 设置i的位置
											lostDataStartIndex = i;

											byte[] lostData = new byte[ii];
											System.arraycopy(recvbuff, 0, lostData, 0, lostData.length);
											Log.debug("丢失的数据4:" + Util.convertByteArrayToHexString(lostData));
											isC = true;
											this.recvbuff = null;
											break;
										}
									}
								}
							}// end for
							if (isC) {
								continue;
							}
						}
						break;
					}
					continue;
				}// end if 查找到第一个00H
				if (i == m_recvBytes.length - 1) {
					byte[] lostData = new byte[m_recvBytes.length - lostDataStartIndex];
					System.arraycopy(m_recvBytes, lostDataStartIndex, lostData, 0, lostData.length);
					Log.debug("丢失的数据5:" + Util.convertByteArrayToHexString(lostData));
				}
				i++;
			}
		}
	}

	public int getMessageID(byte[] msg) {
		return getRxMessageID(msg);
	}

	static int getRxMessageID(byte[] msg) {
		if (msg == null || msg.length < 5) {
			return 0;
		}
		int t = msg[2] & 0xFF;
		if (msg[2] == 0x09) {
			t = 0x0900 | (int) msg[3];
		}
		if (msg[2] == 0x08) {
			t = 0x0800 | (int) msg[3];
		}
		if(msg[2] == ((byte)0xB0)){
			t = 0xB000 | (int)msg[3];
		}
		return t;
	}

	public static byte[] getRxMessageData(byte[] msg) {
		if (msg == null || msg.length <= 6) {
			return null;
		}
		byte[] d = null;
		if (msg[2] == 0x09 || msg[2] == 0x08 || msg[2] == ((byte)0xB0)) {
			if (msg.length <= 7) {
				return null;
			}
			d = new byte[msg.length - 7];
			System.arraycopy(msg, 5, d, 0, d.length);
		} else {
			d = new byte[msg.length - 6];
			System.arraycopy(msg, 4, d, 0, d.length);
		}
		return d;
	}

	/**
	 *
	 * 数据内容有55H和56H的处理，将“55H”转成“56H＋56H”，“56H”转成“56H＋57H” 增加桢头“0x55”
	 *
	 * @param buff
	 * @return
	 */
	public static byte[] formatData(byte[] buff) {
		int count = buff.length;
		for (byte b : buff) {
			if (b == 0x55 || b == 0x56)
				count++;
		}
		byte[] m = new byte[count + 1];
		m[0] = 0x55;
		if (count > buff.length) {
			int p = 1;
			for (byte b : buff) {
				if (b == 0x55) {
					m[p] = 0x56;
					p++;
					m[p] = 0x56;
				} else if (b == 0x56) {
					m[p] = 0x56;
					p++;
					m[p] = 0x57;
				} else {
					m[p] = b;
				}
				p++;
			}
		} else {
			System.arraycopy(buff, 0, m, 1, count);
		}
		return m;
	}

	/**
	 * 将“56H＋56H”转成“55H”，“56H＋57H”转成“56H”
	 *
	 * @param buff
	 *            要转换的数据
	 * @return 转换后的数据
	 */
	static byte[] desFormatData(byte[] buff) {
		int n = 0;// 被替换的次数，即计算有多少次“56H＋56H”或“56H＋57H”
		// 计算n值
		for (int i = 0; i < buff.length - 1; i++) {
			if (buff[i] == 0x56) {
				if (buff[i + 1] == 0x56 || buff[i + 1] == 0x57) {
					n++;
					i++;
				}
			}
		}
		if (n == 0) {
			return buff;// 没有替换字符则返回原来的数组
		}
		byte[] desBuff = new byte[buff.length - n];
		int m = 0;// desBuff下标
		for (int i = 0; i < buff.length - 1; i++) {
			if (buff[i] == 0x56) {
				if (buff[i + 1] == 0x56) {
					desBuff[m] = 0x55;
				} else if (buff[i + 1] == 0x57) {
					desBuff[m] = 0x56;
				}
				i++;
			} else {
				desBuff[m] = buff[i];
			}
			m++;
		}// end for
		if (m < desBuff.length) {
			desBuff[m] = buff[buff.length - 1];
		}
		return desBuff;
	}

}