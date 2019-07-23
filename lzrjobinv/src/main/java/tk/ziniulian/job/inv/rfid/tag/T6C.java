package tk.ziniulian.job.inv.rfid.tag;

import invengo.javaapi.protocol.IRP1.RXD_TagData;
import tk.ziniulian.util.Str;

/**
 * 标签基类
 * Created by LZR o 2017/8/9.
 */

public class T6C {
	private byte[] epc = null;
	private byte[] tid = null;
	private byte[] use = null;
	private byte[] bck = null;

	protected int ewl = 0;
	protected int twl = 0;
	protected int uwl = 0;
	protected int bwl = 0;
	protected int erl = 0;
	protected int trl = 0;
	protected int upt = 0;	// 用户区起始位置
	protected int url = 0;
	protected int brl = 0;

	private int tim = 1;	// 标签扫描到的次数

	public T6C setEpc(byte[] epc) {
		this.epc = epc;
		return this;
	}

	public T6C setEpc(String hex) {
		return setEpc(Str.Hexstr2Bytes(hex));
	}

	public T6C setEpcDat(String dat) {
		byte[] b;
		if (ewl == 0) {
			b = Str.Dat2Bytes(dat);
		} else {
			b = Str.getLimitBytes(dat, ewl);
		}
		return setEpc(b);
	}

	public T6C setTid(byte[] tid) {
		this.tid = tid;
		return this;
	}

	public T6C setTid(String hex) {
		return setTid(Str.Hexstr2Bytes(hex));
	}

	public T6C setUse(byte[] use) {
//		this.use = use;
//		return this;
		return setUse(0, use);
	}

	public T6C setUse(int offset, byte[] bs) {
		if (bs == null) {
			use = null;
		} else {
			int n;
			int s = bs.length;
			if (uwl == 0) {
				n = s + offset;
			} else {
				n = uwl;
				if ((s + offset) > n) {
					s = n - offset;
				}
			}
			if (use == null) {
				use = new byte[n];
			}
			System.arraycopy(bs, 0, use, offset, s);
		}
		return this;
	}

	public T6C setUse(String hex) {
		return setUse(Str.Hexstr2Bytes(hex));
	}

	public T6C setUse(int offset, String hex) {
		return setUse(offset, Str.Hexstr2Bytes(hex));
	}

	public T6C setUseDat(String dat) {
		byte[] b;
		if (uwl == 0) {
			b = Str.Dat2Bytes(dat);
		} else {
			b = Str.getLimitBytes(dat, uwl);
		}
		return setUse(b);
	}

	public T6C setUseDat(int offset, String dat) {
		byte[] b;
		if (uwl == 0) {
			b = Str.Dat2Bytes(dat);
		} else {
			b = Str.getLimitBytes(dat, uwl - offset);
		}
		return setUse(offset, b);
	}

	public T6C setBck(byte[] bck) {
		this.bck = bck;
		return this;
	}

	public T6C setBck(String hex) {
		return setBck(Str.Hexstr2Bytes(hex));
	}

	public byte[] getEpc() {
		return epc;
	}

	public String getEpcDat() {
		return Str.Bytes2Dat(epc);
	}

	public String getEpcHexstr() {
		return Str.Bytes2Hexstr(epc);
	}

	public byte[] getTid() {
		return tid;
	}

	public String getTidHexstr() {
		return Str.Bytes2Hexstr(tid);
	}

	public byte[] getUse() {
		return use;
	}

	public String getUseDat() {
		return Str.Bytes2Dat(use);
	}

	public String getUseHexstr() {
		return Str.Bytes2Hexstr(use);
	}

	public byte[] getBck() {
		return bck;
	}

	public String getBckDat() {
		return Str.Bytes2Dat(bck);
	}

	public String getBckHexstr() {
		return Str.Bytes2Hexstr(bck);
	}

	protected void hdJson (StringBuilder sb, boolean isHex) {
		if (epc != null) {
			sb.append("\"epc\":");
			sb.append('\"');
			if (isHex) {
				sb.append(getEpcHexstr());
			} else {
				sb.append(getEpcDat());
			}
			sb.append('\"');
			sb.append(',');
		}
		if (tid != null) {
			sb.append("\"tid\":");
			sb.append('\"');
			sb.append(getTidHexstr());
			sb.append('\"');
			sb.append(',');
		}
		if (use != null) {
			sb.append("\"use\":");
			sb.append('\"');
			if (isHex) {
				sb.append(getUseHexstr());
			} else {
				sb.append(getUseDat());
			}
			sb.append('\"');
			sb.append(',');
		}
		if (bck != null) {
			sb.append("\"bck\":");
			sb.append('\"');
			if (isHex) {
				sb.append(getBckHexstr());
			} else {
				sb.append(getBckDat());
			}
			sb.append('\"');
			sb.append(',');
		}
	}

	public String toJson(boolean isHex) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"tim\":");
		sb.append(tim);
		sb.append(',');

		hdJson(sb, isHex);

		sb.deleteCharAt(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}

	public int getTim() {
		return tim;
	}

	public void addOne () {
		tim ++;
	}

	public void setByRXD_TagData_ReceivedInfo (RXD_TagData.ReceivedInfo ri) {
		setEpc(ri.getEPC());
		setTid(ri.getTID());
		setUse(ri.getUserData());
		setBck(ri.getReserved());
	}

	// 获取TID的读取长度（单位：字，双字节）
	public byte getTidWord () {
		int r = trl / 2;
		return (byte)r;	// 整除
	}

	// 获取用户区的读取长度（单位：字，双字节）
	public byte getUseWord () {
		int r = url / 2;
		return (byte)r;	// 整除
	}

	// 获取用户区的起始位置（单位：字，双字节）
	public byte getUptWord () {
		int r = upt / 2;
		return (byte)r;	// 整除
	}
}
