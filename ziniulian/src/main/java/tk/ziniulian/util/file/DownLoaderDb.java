package tk.ziniulian.util.file;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tk.ziniulian.util.EnumMgr;
import tk.ziniulian.util.dao.DbLocal;

import static tk.ziniulian.util.Str.meg;

/**
 * 下载器相关的数据库
 * Created by 李泽荣 on 2019/7/5.
 */

public class DownLoaderDb extends DbLocal {
	public DownLoaderDb(Context c, int version) {
		super(c, version);
	}
	public DownLoaderDb(Context c, int version, boolean noSd) {
		super(c, version, noSd);
	}

	/*
		// 相关的数据库操作 ：
		Fl(	// 文件存储键值对
			"create table Fl(" +	// 表名
			"fid text primary key not null, " +	// 文件服务端ID
			"did number," +	// 文件下载ID
			"typ text)");	// 文件类型

		// 添加文件记录
		FlAdd("insert into Fl values('<0>',<1>, '<2>')"),

		// 通过文件ID获取下载ID
		FlDid("select did from Fl where fid = '<0>'"),

		// 通过下载ID获取文件类型
		FlTyp("select typ from Fl where did = <0>");
	*/

	// 添加文件记录
	public void addFl (String fid, Long did, String typ) {
		exe(EnumMgr.getEmByK("EmLocalSql", "FlAdd"), fid, did + "", typ);
	}

	// 获取下载ID
	public Long getDid (String fid) {
		Long r = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(
				EnumMgr.getEmByK("EmLocalSql", "FlDid").toString(),
				fid
		), null);

		if (c.moveToNext()) {
			r = c.getLong(0);
		}

		c.close();
		db.close();
		return r;
	}

	// 获取文件类型
	public String getTyp (Long did) {
		String r = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(
				EnumMgr.getEmByK("EmLocalSql", "FlTyp").toString(),
				did + ""
		), null);

		if (c.moveToNext()) {
			r = c.getString(0);
		}

		c.close();
		db.close();
		return r;
	}
}
