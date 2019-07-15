package tk.ziniulian.util;

import java.util.HashMap;

/**
 * 枚举管理器
 * Created by 李泽荣 on 2019/7/4.
 */

public class EnumMgr {
	private static HashMap<String, Class> ms = new HashMap<String, Class>();

	public static boolean add (Class c) {
		String nam = c.getSimpleName();
		if (!ms.containsKey(nam)) {
			ms.put(nam, c);
			return true;
		}
		return false;
	}

	// 反射获取某枚举的所有常量
	public static Enum[] values (String cnam) {
		if (ms.containsKey(cnam)) {
			return (Enum[]) ms.get(cnam).getEnumConstants();
		}
		return null;
	}

	// 通过枚举名反射获取某枚举类的某个常量
	public static Enum getEmByK (String cnam, String k) {
		if (ms.containsKey(cnam)) {
			try {
				return (Enum) ms.get(cnam).getMethod("valueOf", String.class).invoke(null, k);
			} catch (Exception e) {}
		}
		return null;
	}

	// 通过序号反射获取某枚举类的某个常量
	public static Enum getEmById (String cnam, int id) {
		if (ms.containsKey(cnam)) {
			Object[] r = ms.get(cnam).getEnumConstants();
			if (r != null && r.length > id) {
				return (Enum) r[id];
			}
		}
		return null;
	}

	// 通过枚举名反射获取某枚举类的某个常量的序号
	public static Integer getIdByK (String cnam, String k) {
		if (ms.containsKey(cnam)) {
			try {
				Object e = ms.get(cnam).getMethod("valueOf", String.class).invoke(null, k);
				if (e != null) {
					return ((Enum)e).ordinal();
				}
			} catch (Exception e) {}
		}
		return null;
	}

	// 测试程序
	public static void main (String[] args) {
		EnumMgr.add(EmLocalCrtSql.class);
		System.out.println(EnumMgr.getEmByK("EmLocalCrtSql", "dbNam"));
		System.out.println(EnumMgr.getEmById("EmLocalCrtSql", 0));
		System.out.println(EnumMgr.getIdByK("EmLocalCrtSql", "Bkv"));

		System.out.println(EnumMgr.getEmByK("EmLocalCrtSql", "dbNam---"));
		System.out.println(EnumMgr.getEmById("EmLocalCrtSql", 100));
		System.out.println(EnumMgr.getIdByK("EmLocalCrtSql", "--Bkv"));

		Enum[] es = EnumMgr.values("EmLocalCrtSql");
		for (Enum e : es) {
			System.out.println(e.ordinal() + " : " + e.name() + " , " + e.toString());
		}
	}

	// 数据库建表枚举示例
	private enum EmLocalCrtSql {
		sdDir("Invengo/FlShar/DB/"),	// 数据库存储路径

		dbNam("flShar.db"),	// 数据库名

		Bkv(	// 基本键值对表
			"create table Bkv(" +	// 表名
			"k text primary key not null, " +	// 键
			"v text)");	// 值

		private final String sql;
		EmLocalCrtSql(String s) {
			sql = s;
		}

		@Override
		public String toString() {
			return sql;
		}
	}

	// 数据库查询枚举示例
	private enum EmLocalSql {
		// 获取键值对
		KvGet("select v from <1> where k = '<0>'"),

		// 设置键值对
		KvSet("update <2> set v = '<1>' where k = '<0>'"),

		// 添加键值对
		KvAdd("insert into <2> values('<0>', '<1>')"),

		// 删除键值对
		KvDel("delete from <1> where k = '<0>'");

		private final String sql;
		EmLocalSql(String s) {
			sql = s;
		}

		@Override
		public String toString() {
			return sql;
		}
	}

}
