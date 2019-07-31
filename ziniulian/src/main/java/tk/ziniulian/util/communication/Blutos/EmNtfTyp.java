package tk.ziniulian.util.communication.Blutos;

/**
 * BLE 蓝牙 notify 类型
 * Created by 李泽荣 on 2019/7/30.
 */

public enum EmNtfTyp {
	WR(0x01),	// 读写
	POW(0x02),	// 电量

	/********* 组合 ********/
	ALL(0x03),	// 全部
	;

	private int id;
	EmNtfTyp(int i) {
		id = i;
	}

	public int gid() {
		return id;
	}

	// 类型匹配
	public boolean match (int t) {
		return (t & this.id) != 0;
	}
	public boolean match (EmNtfTyp t) {
		return match(t.id);
	}
}
